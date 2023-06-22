package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception401;
import com.triplea.triplea.core.exception.Exception404;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.core.util.StepPaySubscriber;
import com.triplea.triplea.core.util.provide.MoyaNewsProvider;
import com.triplea.triplea.core.util.provide.TiingoStockProvider;
import com.triplea.triplea.core.util.provide.symbol.MoyaSymbolProvider;
import com.triplea.triplea.core.util.provide.symbol.TiingoSymbolProvider;
import com.triplea.triplea.core.util.timestamp.Timestamped;
import com.triplea.triplea.core.util.translate.Papago;
import com.triplea.triplea.core.util.translate.WiseSTGlobal;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.category.CategoryResponse;
import com.triplea.triplea.dto.news.ApiResponse;
import com.triplea.triplea.dto.news.NewsRequest;
import com.triplea.triplea.dto.news.NewsResponse;
import com.triplea.triplea.dto.stock.StockResponse;
import com.triplea.triplea.dto.symbol.SymbolRequest;
import com.triplea.triplea.dto.symbol.SymbolResponse;
import com.triplea.triplea.dto.user.UserResponse;
import com.triplea.triplea.model.bookmark.BookmarkNews;
import com.triplea.triplea.model.bookmark.BookmarkNewsRepository;
import com.triplea.triplea.model.category.CategoryRepository;
import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.category.MainCategoryRepository;
import com.triplea.triplea.model.customer.Customer;
import com.triplea.triplea.model.customer.CustomerRepository;
import com.triplea.triplea.model.history.History;
import com.triplea.triplea.model.history.HistoryRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.triplea.triplea.dto.news.ApiResponse.Data;
import static com.triplea.triplea.dto.news.ApiResponse.GlobalNewsDTO;
import static com.triplea.triplea.dto.news.NewsResponse.NewsDTO;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NewsService {

    private final BookmarkNewsRepository bookmarkNewsRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final HistoryRepository historyRepository;
    private final MoyaNewsProvider newsProvider;
    private final MoyaSymbolProvider moyaSymbolProvider;
    private final TiingoSymbolProvider tiingoSymbolProvider;
    private final TiingoStockProvider stockProvider;
    private final StepPaySubscriber subscriber;
    private final Papago papagoTranslator;
    private final WiseSTGlobal wiseTranslator;
    private final RedisTemplate<String, String> redisTemplate;

    private final int globalNewsMaxSize = 100;
    @Value("${moya.token}")
    private String moyaToken;

    @Transactional(readOnly = true)
    public NewsResponse.News searchAllNews(User user, int Size, long page) {

        if (Size > globalNewsMaxSize) {
            throw new Exception400("Size", "Request exceeds maximum data size " + globalNewsMaxSize);
        }

        //API 쿼리 파라미터 의미
        //limit: 가져올 뉴스 개수
        //offset: 받은 Data 배열 인덱스 0 부터의 거리. 100이면 100번째 뉴스부터 limit 개수만큼 받는다
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/globalnews")
                .queryParam("token", moyaToken)
                .queryParam("limit", Size);

        if (page != 0)
            builder.queryParam("nextPage", page);

        String url = builder.toUriString();

        ResponseEntity<GlobalNewsDTO> response = restTemplate.getForEntity(url, GlobalNewsDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            GlobalNewsDTO globalNewsDTO = response.getBody();

            List<Data> datas = globalNewsDTO.getDatas();

            List<NewsDTO> newsDTOList = new ArrayList<>();
            for (Data data : datas) {
                List<BookmarkNews> bookmarkNewsList = bookmarkNewsRepository.findNonDeletedByNewsId(data.getId());//bookmark의 newsId 같은거 가져와야함
                Optional<BookmarkNews> opBookmark = bookmarkNewsRepository.findNonDeletedByNewsIdAndUserId(data.getId(), user.getId());

                BookmarkResponse.BookmarkDTO bookmarkDTO = new BookmarkResponse.BookmarkDTO(bookmarkNewsList.size(), opBookmark.isPresent());

                builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/stock")
                        .queryParam("token", moyaToken)
                        .queryParam("search", data.getSymbol());

                ResponseEntity<ApiResponse.BookmarkSymbolDTO[]> bsresponse;
                try {
                    bsresponse = restTemplate.getForEntity(builder.toUriString(), ApiResponse.BookmarkSymbolDTO[].class);
                } catch (RestClientException e) {
                    log.error(url, e.getMessage());
                    throw new Exception500("API 호출 실패");
                }

                ApiResponse.BookmarkSymbolDTO[] dtos = bsresponse.getBody();

                String companyName = "";
                if (dtos != null) {
                    for (ApiResponse.BookmarkSymbolDTO dto : dtos) {
                        //symbol 글자 완전 일치하는것만 가져온다
                        if (dto.getSymbol().equals(data.getSymbol().toUpperCase())) {
                            companyName = dto.getCompanyName();
                            break;
                        }
                    }
                }

                newsDTOList.add(new NewsDTO(data, companyName, bookmarkDTO));
            }

            NewsResponse.News news = NewsResponse.News.builder()
                    .news(newsDTOList)
                    .nextPage(globalNewsDTO.getNextPage())
                    .build();

            return news;

        } else {
            // 에러 처리
            throw new Exception500("MOYA API 실패");
        }
    }

    @Transactional(readOnly = true)
    public NewsResponse.News searchSymbolNews(User user, String symbol, int size, long page) {

        if (size > globalNewsMaxSize) {
            throw new Exception400("size", "Request exceeds maximum data size. " + globalNewsMaxSize);
        }

        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/globalnews")
                .queryParam("token", moyaToken)
                .queryParam("symbol", symbol)
                .queryParam("limit", size);

        if (page != 0)
            builder.queryParam("nextPage", page);

        String url = builder.toUriString();

        ResponseEntity<GlobalNewsDTO> response = restTemplate.getForEntity(url, GlobalNewsDTO.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            GlobalNewsDTO globalNewsDTO = response.getBody();

            List<Data> datas = globalNewsDTO.getDatas();

            List<NewsDTO> newsDTOList = new ArrayList<>();
            for (Data data : datas) {
                List<BookmarkNews> bookmarkNewsList = bookmarkNewsRepository.findNonDeletedByNewsId(data.getId());
                Optional<BookmarkNews> opBookmark = bookmarkNewsRepository.findNonDeletedByNewsIdAndUserId(data.getId(), user.getId());

                BookmarkResponse.BookmarkDTO bookmarkDTO = new BookmarkResponse.BookmarkDTO(bookmarkNewsList.size(), opBookmark.isPresent());

                builder = UriComponentsBuilder.fromHttpUrl("https://api.moya.ai/stock")
                        .queryParam("token", moyaToken)
                        .queryParam("search", data.getSymbol());

                ResponseEntity<ApiResponse.BookmarkSymbolDTO[]> bsresponse;
                try {
                    bsresponse = restTemplate.getForEntity(builder.toUriString(), ApiResponse.BookmarkSymbolDTO[].class);
                } catch (RestClientException e) {
                    log.error(url, e.getMessage());
                    throw new Exception500("API 호출 실패");
                }

                ApiResponse.BookmarkSymbolDTO[] dtos = bsresponse.getBody();

                String companyName = "";
                if (dtos != null) {
                    for (ApiResponse.BookmarkSymbolDTO dto : dtos) {
                        //symbol 글자 완전 일치하는것만 가져온다
                        if (dto.getSymbol().equals(data.getSymbol().toUpperCase())) {
                            companyName = dto.getCompanyName();
                            break;
                        }
                    }
                }

                newsDTOList.add(new NewsDTO(data, companyName, bookmarkDTO));
            }

            NewsResponse.News news = NewsResponse.News.builder()
                    .news(newsDTOList)
                    .nextPage(globalNewsDTO.getNextPage())
                    .build();

            return news;

        } else {
            // 에러 처리
            throw new Exception500("MOYA API 실패");
        }
    }

    // 뉴스 조회(키워드)
    public NewsResponse.News getNewsByKeyword(String keyword, int size, Long page, User user) {
        List<Long> newsIds;

        // 키워드로 뉴스 ID 조회
        try (Response keywordResponse = newsProvider.getNewsIdByKeyword(keyword)) {
            newsIds = newsProvider.getNewsId(keywordResponse);
        } catch (Exception e) {
            throw new Exception500("키워드 조회 실패: " + e.getMessage());
        }

        NewsRequest.Page pages = getPages(size, page, newsIds);
        List<NewsResponse.NewsDTO> newsList = getNewsByPage(pages, newsIds, user);

        return NewsResponse.News.builder()
                .search(keyword)
                .nextPage(pages.getNextPage())
                .news(newsList)
                .build();

    }

    // 뉴스 조회(카테고리)
    public NewsResponse.News getNewsByCategory(Long id, int size, Long page, User user) {
        List<Long> newsIds = new ArrayList<>();

        // 카테고리 조회
        MainCategory mainCategory = mainCategoryRepository.findById(id).orElseThrow(
                () -> new Exception404("카테고리를 찾을 수 없습니다"));
        categoryRepository.findCategoriesByMainCategory(mainCategory.getId())
                .forEach(category -> {
                    // 대분류 카테고리에 해당하는 모든 카테고리로 API 요청
                    try (Response categoryResponse = newsProvider.getNewsIdByCategory(category.getCategory())) {
                        // 해당 카테고리의 모든 NewsId 값을 가져옴
                        newsIds.addAll(newsProvider.getNewsId(categoryResponse));
                    } catch (Exception e) {
                        throw new Exception500("카테고리 조회 실패: " + e.getMessage());
                    }
                });

        NewsRequest.Page pages = getPages(size, page, newsIds);
        List<NewsResponse.NewsDTO> newsList = getNewsByPage(pages, newsIds, user);

        return NewsResponse.News.builder()
                .search(mainCategory.getMainCategoryKor())
                .nextPage(pages.getNextPage())
                .news(newsList)
                .build();
    }

    // 뉴스 상세 조회
    @Transactional
    public NewsResponse.Details getNewsDetails(Long id, User user) {
        user = getUser(user);

        ApiResponse.Details details;
        // 뉴스 ID로 상세 조회
        try (Response response = newsProvider.getNewsById(id)) {
            details = newsProvider.getNewsDetails(response);
        } catch (Exception e) {
            throw new Exception500("뉴스 상세 조회 실패: " + e.getMessage());
        }

        // 히스토리 생성
        saveHistory(user, id);

        CategoryResponse category = getCategory(details.getCategory());

        // symbol 상세 정보 조회
        String symbol = details.getSymbol();
        SymbolRequest.MoyaSymbol moyaSymbol = getSymbolInfo(symbol);
        String logo = moyaSymbolProvider.getLogo(moyaSymbol);

        // Tiingo API에서 어제와 오늘에 대한 주가 정보 조회
        StockResponse.Price price = getStockPrices(symbol);
        SymbolResponse.News newsSymbol = SymbolResponse.News.builder()
                .symbol(moyaSymbol)
                .logo(logo)
                .price(price)
                .build();

        // 일반 회원 베네핏 설정
        User.Membership membership = getMembership(user);
        String key = "news_" + user.getEmail(); int benefitCount = 10;
        List<Long> newsId = getNewsIdForBasicMembership(details.getDescription(), membership, key, benefitCount, id);
        NewsResponse.TranslateOut.Article articles = getArticles(isArticleViewable(membership, newsId, id), details);
        NewsResponse.Details.Article articleEng = articles.getArticleEng();
        NewsResponse.Details.Article articleKor = articles.getArticleKor();

        // 남은 Benefit Count: User 의 Membership 이 PREMIUM 이면 null
        Integer leftBenefitCount = leftBenefitsForBasicMembership(membership, newsId, benefitCount);
        UserResponse.News userMembership = UserResponse.News.builder()
                .membership(user.getMembership())
                .leftBenefitCount(leftBenefitCount)
                .historyNewsIds(newsId).build();

        return NewsResponse.Details.builder()
                .user(userMembership)
                .symbol(newsSymbol)
                .details(details)
                .eng(articleEng)
                .kor(articleKor)
                .category(category)
                .bookmark(getBookmark(id, user))
                .build();
    }

    // 히스토리 조회
    public List<NewsResponse.HistoryOut> getHistory(int year, int month, User user) {
        List<ZonedDateTime> historyDateTimes = historyRepository.findDateTimeByCreatedAtAndUser(year, month, user);

        return historyDateTimes.stream().map(dateTime -> {
            LocalDate date = dateTime.toLocalDate();

            List<NewsResponse.HistoryOut.Bookmark.News> bookmarkNews = bookmarkNewsRepository.findByCreatedAtAndUser(date, user.getId())
                    .stream()
                    .map(bookmark -> NewsResponse.HistoryOut.Bookmark.News.builder()
                            .id(bookmark.getNewsId())
                            .isDeleted(bookmark.isDeleted())
                            .build())
                    .collect(Collectors.toList());

            List<NewsResponse.HistoryOut.History.News> historyNews = historyRepository.findByCreatedAtAndUser(date, user.getId())
                    .stream()
                    .map(history -> new NewsResponse.HistoryOut.History.News(history.getNewsId()))
                    .collect(Collectors.toList());

            NewsResponse.HistoryOut.Bookmark bookmarkOut = NewsResponse.HistoryOut.Bookmark.builder()
                    .count(bookmarkNews.size())
                    .news(bookmarkNews)
                    .build();

            NewsResponse.HistoryOut.History historyOut = NewsResponse.HistoryOut.History.builder()
                    .count(historyNews.size())
                    .news(historyNews)
                    .build();

            return NewsResponse.HistoryOut.builder()
                    .date(date)
                    .bookmark(bookmarkOut)
                    .history(historyOut)
                    .build();
        }).collect(Collectors.toList());
    }

    // AI 뉴스 분석
    @Transactional
    public NewsResponse.Analysis getAnalysisAI(Long id, NewsRequest.AI ai, User user) {
        user = getUser(user);

        // 베네핏 설정
        User.Membership membership = getMembership(user);
        if (membership != User.Membership.PREMIUM) throw new Exception401("유료 회원만 사용할 수 있습니다");
        String key = "ai_" + user.getEmail(); int benefitCount = 10;

        NewsResponse.Analysis analysis;
        // 요청 횟수 redis 에 저장해서 남은 베네핏 수와 비교
        int count = countsAIBenefit(key, false);
        int leftCount = saveCountsAIBenefit(key, benefitCount, count, false);

        // AI 분석 API 요청
        String summary = ai.getSummary();
        try (Response response = wiseTranslator.analysis(id, summary)) {
            analysis = wiseTranslator.getAnalysis(response);
            analysis.leftBenefitCount(leftCount);
        } catch (Exception e) {
            // 에러로 AI 분석이 실패한 경우 rollback
            count = countsAIBenefit(key, true);
            saveCountsAIBenefit(key, benefitCount, count, true);
            throw new Exception500("AI 분석 실패: " + e.getMessage());
        }
        return analysis;
    }


    private User getUser(User user) {
        return userRepository.findById(user.getId()).orElseThrow(
                () -> new Exception401("잘못된 접근입니다"));
    }

    private void saveHistory(User user, Long newsId) {
        // 같은 날엔 뉴스당 한 번의 히스토리 내역만 저장
        ZonedDateTime today = ZonedDateTime.now(Timestamped.SEOUL_ZONE_ID);
        boolean historyExists = historyRepository.existsByCreatedAtAndUserAndNewsId(today.toLocalDate(), user, newsId);
        try {
            if (!historyExists){
                History history = History.builder().user(user).newsId(newsId).build();
                historyRepository.save(history);
            }
        } catch (Exception e) {
            throw new Exception500("history 저장 실패: " + e.getMessage());
        }
    }

    /**
     * @param size    페이지별 조회할 뉴스의 갯수
     * @param page    현재 페이지
     * @param newsIds 찾은 모든 NewsId
     * @return NewsRequest.Page
     */
    private NewsRequest.Page getPages(int size, Long page, List<Long> newsIds) {
        int totalNewsCount = newsIds.size();
        int startIndex = size * (page.intValue());
        int endIndex = Math.min(startIndex + size, totalNewsCount);
        Long nextPage = null;
        if (endIndex < totalNewsCount) nextPage = page + 1;
        return NewsRequest.Page.builder()
                .startIndex(startIndex)
                .endIndex(endIndex)
                .nextPage(nextPage)
                .build();
    }

    /**
     * 페이지네이션이 되지 않는 API의 경우 임의로 페이지네이션 구현
     * @param pages   page
     * @param newsIds 찾은 모든 NewsId
     * @param user    북마크를 한 뉴스인지 확인하기 위한 유저 정보
     * @return 뉴스 목록
     */
    private List<NewsResponse.NewsDTO> getNewsByPage(NewsRequest.Page pages, List<Long> newsIds, User user) {
        if (newsIds.isEmpty()) return Collections.emptyList();
        int startIndex = pages.getStartIndex();
        int endIndex = pages.getEndIndex();

        // pagination 구현
        List<Long> newsIdsSubset = newsIds.subList(startIndex, endIndex);
        return newsIdsSubset.stream()
                .map(newsId -> {
                    // 뉴스 ID로 뉴스 조회
                    try (Response newsResponse = newsProvider.getNewsById(newsId)) {
                        ApiResponse.Details newsDetails = newsProvider.getNewsDetails(newsResponse);
                        SymbolRequest.MoyaSymbol moyaSymbol = moyaSymbolProvider.getSymbolInfo(newsDetails.getSymbol());
                        if (moyaSymbol == null || moyaSymbol.getCompanyName() == null)
                            moyaSymbol = tiingoSymbolProvider.getSymbolInfo(newsDetails.getSymbol());
                        String companyName = moyaSymbol.getCompanyName();
                        String logo = moyaSymbolProvider.getLogo(moyaSymbol);
                        return NewsResponse.NewsDTO.builder()
                                .details(newsDetails)
                                .companyName(companyName)
                                .logo(logo)
                                .bookmark(getBookmark(newsId, user))
                                .build();
                    } catch (IOException e) {
                        throw new Exception500("뉴스 조회 실패: " + e.getMessage());
                    }
                }).collect(Collectors.toList());
    }

    /**
     * @param newsId 뉴스 ID
     * @param user   로그인한 유저
     * @return 북마크여부(boolean), 총 북마크 수
     */
    private BookmarkResponse.BookmarkDTO getBookmark(Long newsId, User user) {
        // 내가 북마크한 뉴스인지 여부
        boolean isBookmark = user != null & bookmarkNewsRepository.findNonDeletedByNewsIdAndUserId(newsId, user.getId()).isPresent();
        // 총 북마크한 수
        int bookmarkCount = bookmarkNewsRepository.countBookmarkNewsByNewsId(newsId);

        return BookmarkResponse.BookmarkDTO.builder()
                .isBookmark(isBookmark)
                .count(bookmarkCount)
                .build();
    }

    private Integer leftBenefitsForBasicMembership(User.Membership membership, List<Long> newsId, int benefitCount) {
        return membership != User.Membership.BASIC ? null : benefitCount - newsId.size();
    }

    private List<Long> getNewsIdForBasicMembership(String description, User.Membership membership, String key, int benefitCount, Long id) {
        if (membership != User.Membership.BASIC) return null;

        List<Long> newsId = new ArrayList<>();
        String storedNewsId = redisTemplate.opsForValue().get(key);
        if (storedNewsId != null) newsId = Arrays.stream(storedNewsId.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        if (description != null && newsId.size() < benefitCount) {
            if (newsId.size() == 0 || newsId.stream().noneMatch(news -> news.equals(id))) {
                newsId.add(id);
                String serializedNewsId = StringUtils.collectionToCommaDelimitedString(newsId);
                redisTemplate.opsForValue().set(key, serializedNewsId);
            }
            redisExpirationAtMidnight(key);
        }
        return newsId;
    }

    private Integer countsAIBenefit(String key, boolean isBack) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) value = "0";
        if (!value.equals("0") && isBack) return Integer.parseInt(value) - 1;
        return Integer.parseInt(value) + 1;
    }

    private Integer saveCountsAIBenefit(String key, int benefitCount, int count, boolean isBack) {
        int leftCount = benefitCount - count;
        if (!isBack && leftCount < 0) throw new Exception400("Benefit", "오늘 혜택을 다 소진했습니다");
        redisTemplate.opsForValue().set(key, String.valueOf(count));
        redisExpirationAtMidnight(key);
        return leftCount;
    }

    private boolean isArticleViewable(User.Membership membership, List<Long> newsId, Long id) {
        return membership != User.Membership.BASIC || newsId.stream().anyMatch(news -> news.equals(id));
    }

    private void redisExpirationAtMidnight(String key) {
        ZonedDateTime now = ZonedDateTime.now(Timestamped.SEOUL_ZONE_ID);
        ZonedDateTime midnight = ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, Timestamped.SEOUL_ZONE_ID);
        if (now.isAfter(midnight)) {
            midnight = midnight.plusDays(1L);
        }

        Duration duration = Duration.between(now, midnight);
        long secondsUntilMidnight = duration.getSeconds();
        redisTemplate.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
    }

    private User.Membership getMembership(User user) {
        if (user.getMembership() == User.Membership.PREMIUM && !checkSubscription(user)) {
            Customer customer = getCustomer(user);
            customer.deactivateSubscription();
//            user.changeMembership(User.Membership.BASIC);
        }

        return user.getMembership();
    }

    private boolean checkSubscription(User user) {
        Long subscriptionId = customerRepository.findCustomerByUserId(user.getId()).map(Customer::getSubscriptionId).orElse(null);
        if (subscriptionId == null) return false;
        try {
            return subscriber.isSubscribe(subscriptionId);
        } catch (Exception e) {
            throw new Exception500("구독 확인 실패: " + e.getMessage());
        }
    }

    /**
     * user id로 customer 찾고 없으면 예외처리
     */
    private Customer getCustomer(User user) {
        return customerRepository.findCustomerByUserId(user.getId()).orElseThrow(
                () -> new Exception400("customer", "잘못된 요청입니다"));
    }

    private CategoryResponse getCategory(String category) {
        // 해당하는 대분류 카테고리(한글명)를 찾아서 return
        if (category == null) return null;
        MainCategory mainCategory = mainCategoryRepository.findMainCategoryBySubCategory(category).orElse(new MainCategory());
        return CategoryResponse.builder()
                .categoryId(mainCategory.getId())
                .category(mainCategory.getMainCategoryKor())
                .build();
    }

    private SymbolRequest.MoyaSymbol getSymbolInfo(String symbol) {
        SymbolRequest.MoyaSymbol moyaSymbol = moyaSymbolProvider.getSymbolInfo(symbol);
        if (moyaSymbol == null || moyaSymbol.getCompanyName() == null)
            moyaSymbol = tiingoSymbolProvider.getSymbolInfo(symbol);
        return moyaSymbol;
    }

    private StockResponse.Price getStockPrices(String symbol) {
        ZonedDateTime today = ZonedDateTime.now(Timestamped.EST_ZONE_ID);
        ZonedDateTime yesterday = today.minusDays(7); //미국 주식 시장이 안 열리는 상황을 대비해서 일주일 데이터를 가져와서 최신 2건을 return 하는 것으로 수정
        return stockProvider.getStocks(symbol, yesterday.toLocalDate(), today.toLocalDate());
    }

    private NewsResponse.TranslateOut.Article getArticles(boolean isArticleViewable, ApiResponse.Details details) {
        NewsResponse.Details.Article articleEng;
        NewsResponse.Details.Article articleKor;
        if (isArticleViewable) {
            // 혜택에 제한이 없는 경우 전체 기사 번역 및 출력
            NewsResponse.TranslateOut translate = translateArticle(details);
            articleEng = new NewsResponse.Details.Article(details);
            articleKor = new NewsResponse.Details.Article(translate);
        } else {
            // 혜택에 제한이 있는 경우 제목만 번역 및 출력
            articleEng = new NewsResponse.Details.Article(details.getTitle());
            articleKor = new NewsResponse.Details.Article(papagoTranslator.translate(details.getTitle()));
        }
        return NewsResponse.TranslateOut.Article.builder()
                .articleEng(articleEng)
                .articleKor(articleKor)
                .build();
    }

    private NewsResponse.TranslateOut translateArticle(ApiResponse.Details details) {
        try {
            return wiseTranslator.translateArticle(details);
        } catch (Exception e) {
            return NewsResponse.TranslateOut.builder()
                    .title(papagoTranslator.translate(details.getTitle()))
                    .description(papagoTranslator.translate(details.getDescription()))
                    .summary(papagoTranslator.translate(details.getSummary()))
                    .content(papagoTranslator.translate(details.getContent()))
                    .build();
        }
    }
}

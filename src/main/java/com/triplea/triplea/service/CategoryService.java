package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.dto.category.CategoryResponse;
import com.triplea.triplea.model.bookmark.BookmarkCategory;
import com.triplea.triplea.model.bookmark.BookmarkCategoryRepository;
import com.triplea.triplea.model.category.Category;
import com.triplea.triplea.model.category.CategoryRepository;
import com.triplea.triplea.model.category.MainCategory;
import com.triplea.triplea.model.category.MainCategoryRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final BookmarkCategoryRepository bookmarkCategoryRepository;
    private final UserRepository userRepository;
    private String[] categories;
    private HashMap<String, String> hm = new HashMap<>();

    // Category Data init
    @Transactional
    public void insertMainCategories() {
        String str = "1\n" +
                "/News\n" +
                "2\n" +
                "/Arts & Entertainment/TV & Video/Online Video\n" +
                "3\n" +
                "/Finance/Investing\n" +
                "4\n" +
                "/News/Politics\n" +
                "5\n" +
                "/Business & Industrial\n" +
                "6\n" +
                "/Finance/Investing/Currencies & Foreign Exchange\n" +
                "7\n" +
                "/Food & Drink/Beverages/Coffee & Tea\n" +
                "8\n" +
                "/Sports/Team Sports/Soccer\n" +
                "9\n" +
                "/People & Society/Social Issues & Advocacy\n" +
                "10\n" +
                "/Computers & Electronics/Consumer Electronics/Audio Equipment\n" +
                "11\n" +
                "/Health/Health Foundations & Medical Research\n" +
                "12\n" +
                "/Computers & Electronics/Software/Operating Systems\n" +
                "13\n" +
                "/Sensitive Subjects\n" +
                "14\n" +
                "/Food & Drink/Beverages\n" +
                "15\n" +
                "/Finance/Insurance\n" +
                "16\n" +
                "/Finance/Credit & Lending/Loans\n" +
                "17\n" +
                "/Food & Drink/Restaurants\n" +
                "18\n" +
                "/Sports/Team Sports/Baseball\n" +
                "19\n" +
                "/Arts & Entertainment/Movies\n" +
                "20\n" +
                "/Arts & Entertainment/TV & Video/TV Shows & Programs\n" +
                "21\n" +
                "/Home & Garden\n" +
                "22\n" +
                "/Travel/Air Travel\n" +
                "23\n" +
                "/Computers & Electronics\n" +
                "24\n" +
                "/News/Business News/Company News\n" +
                "25\n" +
                "/Food & Drink\n" +
                "26\n" +
                "/Arts & Entertainment/Music & Audio\n" +
                "27\n" +
                "/Autos & Vehicles/Motor Vehicles (By Type)\n" +
                "28\n" +
                "/Business & Industrial/Pharmaceuticals & Biotech\n" +
                "29\n" +
                "/Games\n" +
                "30\n" +
                "/Games/Computer & Video Games\n" +
                "31\n" +
                "/Sports/Motor Sports\n" +
                "32\n" +
                "/Computers & Electronics/Computer Hardware/Computer Components\n" +
                "33\n" +
                "/Finance/Accounting & Auditing\n" +
                "34\n" +
                "/Finance/Investing/Stocks & Bonds\n" +
                "35\n" +
                "/Sports/Individual Sports/Golf" +
                "36\n" +
                "/Autos & Vehicles/Motor Vehicles (By Type)/Hybrid & Alternative Vehicles\n" +
                "37\n" +
                "/Shopping/Apparel/Footwear\n" +
                "38\n" +
                "/Finance\n" +
                "39\n" +
                "/Games/Computer & Video Games/Sandbox Games\n" +
                "40\n" +
                "/Business & Industrial/Business Services\n" +
                "41\n" +
                "/Reference/Humanities/History\n" +
                "42\n" +
                "/Online Communities\n" +
                "43\n" +
                "/Beauty & Fitness/Hair Care\n" +
                "44\n" +
                "/Games/Card Games/Poker & Casino Games\n" +
                "45\n" +
                "/Arts & Entertainment/Events & Listings/Bars, Clubs & Nightlife\n" +
                "46\n" +
                "/Business & Industrial/Business Finance\n" +
                "47\n" +
                "/Autos & Vehicles/Motor Vehicles (By Type)/Trucks & SUVs\n" +
                "48\n" +
                "/Sports/Team Sports/Basketball\n" +
                "49\n" +
                "/Finance/Investing/Commodities & Futures Trading\n" +
                "50\n" +
                "/Business & Industrial/Energy & Utilities/Oil & Gas\n" +
                "51\n" +
                "/Business & Industrial/Energy & Utilities\n" +
                "52\n" +
                "/Law & Government/Legal/Legal Services\n" +
                "53\n" +
                "/Sports/Fantasy Sports\n" +
                "54\n" +
                "/Autos & Vehicles/Vehicle Parts & Services\n" +
                "55\n" +
                "/News/Business News\n" +
                "56\n" +
                "/Computers & Electronics/Electronics & Electrical/Electronic Components\n" +
                "57\n" +
                "/News/Business News/Financial Markets News\n" +
                "58\n" +
                "/Arts & Entertainment/Online Media\n" +
                "59\n" +
                "/Books & Literature\n" +
                "60\n" +
                "/Computers & Electronics/Consumer Electronics\n" +
                "61\n" +
                "/Sports/Team Sports/Hockey\n" +
                "62\n" +
                "/Health/Health Conditions/Diabetes\n" +
                "63\n" +
                "/Health/Health Conditions/Infectious Diseases\n" +
                "64\n" +
                "/People & Society\n" +
                "65\n" +
                "/Computers & Electronics/Consumer Electronics/TV & Video Equipment\n" +
                "66\n" +
                "/Hobbies & Leisure/Radio Control & Modeling\n" +
                "67\n" +
                "/Autos & Vehicles\n" +
                "68\n" +
                "/Sports\n" +
                "69\n" +
                "/Business & Industrial/Metals & Mining\n" +
                "70\n" +
                "/Travel/Cruises & Charters\n" +
                "71\n" +
                "/Health\n" +
                "72\n" +
                "/Finance/Banking\n" +
                "73\n" +
                "/Computers & Electronics/Computer Hardware/Laptops & Notebooks\n" +
                "74\n" +
                "/Computers & Electronics/Electronics & Electrical\n" +
                "75\n" +
                "/Law & Government/Government\n" +
                "76\n" +
                "/Health/Oral & Dental Care\n" +
                "77\n" +
                "/Autos & Vehicles/Vehicle Parts & Services/Vehicle Parts & Accessories\n" +
                "78\n" +
                "/Health/Medical Devices & Equipment\n" +
                "79\n" +
                "/Shopping/Apparel\n" +
                "80\n" +
                "/Online Communities/Social Networks\n" +
                "81\n" +
                "/Computers & Electronics/Software\n" +
                "82\n" +
                "/Health/Health Conditions/Cancer\n" +
                "83\n" +
                "/Shopping\n" +
                "84\n" +
                "/Arts & Entertainment/Humor\n" +
                "85\n" +
                "/Arts & Entertainment\n" +
                "86\n" +
                "/Arts & Entertainment/Comics & Animation/Cartoons\n" +
                "87\n" +
                "/Travel/Tourist Destinations/Theme Parks\n" +
                "88\n" +
                "/Science\n" +
                "89\n" +
                "/Jobs & Education/Education/Primary & Secondary Schooling (K-12)\n" +
                "90\n" +
                "/Internet & Telecom/Mobile & Wireless/Mobile Apps & Add-Ons\n" +
                "91\n" +
                "/Business & Industrial/Transportation & Logistics\n" +
                "92\n" +
                "/Health/Nursing/Assisted Living & Long Term Care\n" +
                "93\n" +
                "/Science/Computer Science\n" +
                "94\n" +
                "/Law & Government/Legal\n" +
                "95\n" +
                "/Beauty & Fitness/Fitness\n" +
                "96\n" +
                "/Food & Drink/Beverages/Soft Drinks\n" +
                "97\n" +
                "/Real Estate\n" +
                "98\n" +
                "/Arts & Entertainment/Music & Audio/Pop Music\n" +
                "99\n" +
                "/News/Sports News\n" +
                "100\n" +
                "/Law & Government/Social Services\n" +
                "101\n" +
                "/Computers & Electronics/Computer Security\n" +
                "102\n" +
                "/Arts & Entertainment/Music & Audio/Urban & Hip-Hop\n" +
                "103\n" +
                "/Health/Pharmacy/Drugs & Medications\n" +
                "104\n" +
                "/Travel\n" +
                "105\n" +
                "/Arts & Entertainment/TV & Video\n" +
                "106\n" +
                "/Autos & Vehicles/Motor Vehicles (By Type)/Motorcycles\n" +
                "107\n" +
                "/Law & Government/Public Safety\n" +
                "108\n" +
                "/Arts & Entertainment/Comics & Animation/Comics\n" +
                "109\n" +
                "/Science/Astronomy\n" +
                "110\n" +
                "/Finance/Credit & Lending\n" +
                "111\n" +
                "/Computers & Electronics/Computer Security/Hacking & Cracking\n" +
                "112\n" +
                "/Arts & Entertainment/Music & Audio/Rock Music\n" +
                "113\n" +
                "/Business & Industrial/Agriculture & Forestry/Livestock\n" +
                "114\n" +
                "/Law & Government\n" +
                "115\n" +
                "/Health/Public Health" +
                "116\n" +
                "/Jobs & Education/Education/Colleges & Universities\n" +
                "117\n" +
                "/Books & Literature/Children's Literature\n" +
                "118\n" +
                "/Jobs & Education/Jobs/Job Listings\n" +
                "119\n" +
                "/Computers & Electronics/Consumer Electronics/Game Systems & Consoles\n" +
                "120\n" +
                "/People & Society/Family & Relationships\n" +
                "121\n" +
                "/Arts & Entertainment/Music & Audio/Radio\n" +
                "122\n" +
                "/Internet & Telecom/Mobile & Wireless\n" +
                "123\n" +
                "/People & Society/Religion & Belief\n" +
                "124\n" +
                "/Business & Industrial/Construction & Maintenance\n" +
                "125\n" +
                "/Internet & Telecom\n" +
                "126\n" +
                "/Business & Industrial/Business Finance/Venture Capital\n" +
                "127\n" +
                "/Games/Computer & Video Games/Shooter Games\n" +
                "128\n" +
                "/Food & Drink/Food & Grocery Retailers\n" +
                "129\n" +
                "/Science/Mathematics\n" +
                "130\n" +
                "/Business & Industrial/Aerospace & Defense/Space Technology\n" +
                "131\n" +
                "/Computers & Electronics/Computer Hardware\n" +
                "132\n" +
                "/Internet & Telecom/Email & Messaging\n" +
                "133\n" +
                "/Business & Industrial/Hospitality Industry\n" +
                "134\n" +
                "/People & Society/Social Sciences/Economics\n" +
                "135\n" +
                "/Real Estate/Real Estate Listings/Lots & Land\n" +
                "136\n" +
                "/Business & Industrial/Energy & Utilities/Renewable & Alternative Energy\n" +
                "137\n" +
                "/Internet & Telecom/Service Providers\n" +
                "138\n" +
                "/Law & Government/Military\n" +
                "139\n" +
                "/Reference/Humanities/Philosophy\n" +
                "140\n" +
                "/Law & Government/Public Safety/Law Enforcement\n" +
                "141\n" +
                "/People & Society/Social Sciences/Political Science\n" +
                "142\n" +
                "/Business & Industrial/Automotive Industry\n" +
                "143\n" +
                "/Science/Biological Sciences\n" +
                "144\n" +
                "/Business & Industrial/Chemicals Industry\n" +
                "145\n" +
                "/Beauty & Fitness\n" +
                "146\n" +
                "/Finance/Financial Planning & Management\n" +
                "147\n" +
                "/Health/Reproductive Health\n" +
                "148\n" +
                "/Business & Industrial/Agriculture & Forestry\n" +
                "149\n" +
                "/Sports/Team Sports/Australian Football\n" +
                "150\n" +
                "/Business & Industrial/Business Services/Consulting\n" +
                "151\n" +
                "/Law & Government/Government/Visa & Immigration\n" +
                "152\n" +
                "/Computers & Electronics/Electronics & Electrical/Power Supplies\n" +
                "153\n" +
                "/Health/Health Conditions\n" +
                "154\n" +
                "/Beauty & Fitness/Face & Body Care\n" +
                "155\n" +
                "/Business & Industrial/Industrial Materials & Equipment\n" +
                "156\n" +
                "157\n" +
                "158\n" +
                "159\n" +
                "160\n" +
                "161\n" +
                "162\n" +
                "163\n" +
                "164\n" +
                "165\n" +
                "167\n" +
                "168\n" +
                "169\n" +
                "170\n" +
                "171\n" +
                "172\n" +
                "173\n" +
                "174\n" +
                "175\n" +
                "176\n" +
                "177\n" +
                "178\n" +
                "179\n" +
                "180\n" +
                "181\n" +
                "182\n" +
                "183\n" +
                "184\n" +
                "185\n" +
                "186\n" +
                "187\n" +
                "188\n" +
                "189\n" +
                "190\n" +
                "191\n" +
                "192\n" +
                "193\n" +
                "194\n" +
                "195\n" +
                "/People & Society/Social Issues & Advocacy/Charity & Philanthropy\n" +
                "/Business & Industrial/Business Operations\n" +
                "/Business & Industrial/Hospitality Industry/Food Service\n" +
                "/Internet & Telecom/Service Providers/Cable & Satellite Providers\n" +
                "/Business & Industrial/Manufacturing\n" +
                "/Internet & Telecom/Mobile & Wireless/Mobile Phones\n" +
                "/Arts & Entertainment/Visual Art & Design/Art Museums & Galleries\n" +
                "/Reference/Libraries & Museums/Museums\n" +
                "/News/Weather\n" +
                "/Travel/Car Rental & Taxi Services\n" +
                "/Computers & Electronics/Enterprise Technology\n" +
                "/Sports/Team Sports/American Football\n" +
                "/Health/Health Conditions/Endocrine Conditions\n" +
                "/Jobs & Education/Education/Standardized & Admissions Tests\n" +
                "/Health/Health Conditions/Neurological Conditions\n" +
                "/Sports/Team Sports\n" +
                "/Beauty & Fitness/Face & Body Care/Make-Up & Cosmetics\n" +
                "/Business & Industrial/Printing & Publishing\n" +
                "/Internet & Telecom/Web Services\n" +
                "/Food & Drink/Food\n" +
                "/Arts & Entertainment/Visual Art & Design/Painting\n" +
                "/Finance/Accounting & Auditing/Tax Preparation & Planning\n" +
                "/Business & Industrial/Industrial Materials & Equipment/Heavy Machinery\n" +
                "/Health/Health Conditions/Genetic Disorders\n" +
                "/Finance/Credit & Lending/Credit Reporting & Monitoring\n" +
                "/Arts & Entertainment/Entertainment Industry\n" +
                "/Reference\n" +
                "/Computers & Electronics/Computer Hardware/Computer Drives & Storage\n" +
                "/Travel/Hotels & Accommodations\n" +
                "/Business & Industrial/Transportation & Logistics/Rail Transport\n" +
                "/Food & Drink/Restaurants/Pizzerias\n" +
                "/Health/Medical Facilities & Services/Medical Procedures\n" +
                "/Business & Industrial/Energy & Utilities/Electricity\n" +
                "/Business & Industrial/Transportation & Logistics/Freight & Trucking\n" +
                "/Law & Government/Public Safety/Security Products & Services\n" +
                "/People & Society/Seniors & Retirement\n" +
                "/People & Society/Social Issues & Advocacy/Work & Labor Issues\n" +
                "/Home & Garden/Home Furnishings\n" +
                "/Science/Physics" +
                "196\n" +
                "/Beauty & Fitness/Face & Body Care/Skin & Nail Care\n" +
                "197\n" +
                "/Home & Garden/Home Furnishings/Living Room Furniture\n" +
                "198\n" +
                "/Law & Government/Public Safety/Crime & Justice\n" +
                "199\n" +
                "/Science/Earth Sciences/Geology\n" +
                "200\n" +
                "/Autos & Vehicles/Vehicle Codes & Driving Laws\n" +
                "201\n" +
                "/Computers & Electronics/Programming\n" +
                "202\n" +
                "/Business & Industrial/Transportation & Logistics/Packaging\n" +
                "203\n" +
                "/Health/Substance Abuse/Drug & Alcohol Treatment\n" +
                "204\n" +
                "/Shopping/Auctions\n" +
                "205\n" +
                "/Health/Pharmacy\n" +
                "206\n" +
                "/Food & Drink/Restaurants/Fast Food\n" +
                "207\n" +
                "/Games/Gambling\n" +
                "208\n" +
                "/Shopping/Apparel/Clothing Accessories\n" +
                "209\n" +
                "/Home & Garden/Gardening & Landscaping\n" +
                "210\n" +
                "/Home & Garden/Home Improvement/Flooring\n" +
                "211\n" +
                "/Computers & Electronics/Software/Software Utilities\n" +
                "212\n" +
                "/Business & Industrial/Agriculture & Forestry/Agricultural Equipment\n" +
                "213\n" +
                "/Health/Health Conditions/Pain Management\n" +
                "214\n" +
                "/Shopping/Mass Merchants & Department Stores\n" +
                "215\n" +
                "/Sports/Sporting Goods\n" +
                "216\n" +
                "/Autos & Vehicles/Vehicle Shopping\n" +
                "217\n" +
                "/Health/Medical Facilities & Services\n" +
                "218\n" +
                "/Business & Industrial/Transportation & Logistics/Mail & Package Delivery\n" +
                "219\n" +
                "/Real Estate/Real Estate Listings/Commercial Properties\n" +
                "220\n" +
                "/Jobs & Education/Jobs\n" +
                "221\n" +
                "/Health/Health Conditions/Heart & Hypertension\n" +
                "222\n" +
                "/Shopping/Tobacco Products\n" +
                "223\n" +
                "/Home & Garden/Home Improvement/Construction & Power Tools\n" +
                "224\n" +
                "/Business & Industrial/Business Services/Office Supplies\n" +
                "225\n" +
                "/Hobbies & Leisure\n" +
                "226\n" +
                "/Finance/Credit & Lending/Credit Cards\n" +
                "227\n" +
                "/Pets & Animals/Pets/Dogs\n" +
                "228\n" +
                "/Health/Nutrition/Vitamins & Supplements\n" +
                "229\n" +
                "/Business & Industrial/Chemicals Industry/Plastics & Polymers\n" +
                "230\n" +
                "/Business & Industrial/Business Services/E-Commerce Services\n" +
                "231\n" +
                "/Finance/Insurance/Health Insurance\n" +
                "232\n" +
                "/Finance/Financial Planning & Management/Retirement & Pension\n" +
                "233\n" +
                "/Real Estate/Real Estate Services\n" +
                "234\n" +
                "/Health/Nutrition\n" +
                "235\n" +
                "/Finance/Accounting & Auditing/Billing & Invoicing\n" +
                "236\n" +
                "/Sports/Team Sports/Cricket\n" +
                "237\n" +
                "/Jobs & Education\n" +
                "238\n" +
                "/Home & Garden/Home Appliances\n" +
                "239\n" +
                "/Shopping/Apparel/Athletic Apparel\n" +
                "240\n" +
                "/Food & Drink/Beverages/Alcoholic Beverages\n" +
                "241\n" +
                "/Real Estate/Real Estate Listings\n" +
                "242\n" +
                "/Business & Industrial/Chemicals Industry/Cleaning Agents\n" +
                "243\n" +
                "/Food & Drink/Food/Meat & Seafood\n" +
                "244\n" +
                "/Real Estate/Real Estate Listings/Residential Sales\n" +
                "245\n" +
                "/Beauty & Fitness/Face & Body Care/Perfumes & Fragrances\n" +
                "246\n" +
                "/Business & Industrial/Metals & Mining/Precious Metals\n" +
                "247\n" +
                "/Food & Drink/Food/Snack Foods\n" +
                "248\n" +
                "/Computers & Electronics/Software/Multimedia Software\n" +
                "249\n" +
                "/Computers & Electronics/Networking\n" +
                "250\n" +
                "/Arts & Entertainment/Music & Audio/Music Equipment & Technology\n" +
                "251\n" +
                "/Shopping/Toys\n" +
                "252\n" +
                "/Arts & Entertainment/Entertainment Industry/Film & TV Industry\n" +
                "253\n" +
                "/Arts & Entertainment/Entertainment Industry/Recording Industry\n" +
                "254\n" +
                "/Pets & Animals/Animal Products & Services/Pet Food & Supplies\n" +
                "255\n" +
                "/Jobs & Education/Education\n" +
                "256\n" +
                "/Science/Earth Sciences\n" +
                "257\n" +
                "/Science/Engineering & Technology\n" +
                "258\n" +
                "/Home & Garden/Home Improvement\n" +
                "259\n" +
                "/Computers & Electronics/Networking/VPN & Remote Access" +
                "260\n" +
                "/Hobbies & Leisure/Water Activities/Boating\n" +
                "261\n" +
                "/Arts & Entertainment/Visual Art & Design\n" +
                "262\n" +
                "/Arts & Entertainment/Music & Audio/Classical Music\n" +
                "263\n" +
                "/Home & Garden/Yard & Patio/Lawn Mowers\n" +
                "264\n" +
                "/Sports/Individual Sports/Cycling\n" +
                "265\n" +
                "/Computers & Electronics/Networking/Network Monitoring & Management";
        str = str.replaceAll("\\d+\n", "");
        categories = str.split("\n");

        String category = "Arts & Entertainment\t예술/문화\n" +
                "Autos & Vehicles\t모빌리티\n" +
                "Beauty & Fitness\t뷰티/건강\n" +
                "Books & Literature\t책/공연/전시\n" +
                "Business & Industrial\t비즈니스\n" +
                "Computers & Electronics\t전자/정보통신\n" +
                "Finance\t금융\n" +
                "Food & Drink\t음식/맛집\n" +
                "Games\t게임\n" +
                "Health\t건강\n" +
                "Hobbies & Leisure\t취미/레저\n" +
                "Home & Garden\t홈/가드닝\n" +
                "Internet & Telecom\t인터넷/통신\n" +
                "Jobs & Education\t직업/교육\n" +
                "Law & Government\t정부/법률\n" +
                "News\t뉴스\n" +
                "Online Communities\t커뮤니티\n" +
                "People & Society\t사회 일반\n" +
                "Pets & Animals\t반려동물\n" +
                "Real Estate\t부동산\n" +
                "Reference\t레퍼런스\n" +
                "Science\t과학 일반\n" +
                "Sensitive Subjects\t민감한 주제\n" +
                "Shopping\t쇼핑\n" +
                "Sports\t스포츠\n" +
                "Travel\t여행";
        String[] result = category.split("\n");

        List<MainCategory> mainCategoryList = Arrays.stream(result)
                .map(s -> {
                    String[] cate = s.split("\t");
                    hm.put(cate[0], cate[1]);
                    return MainCategory.builder().mainCategoryEng(cate[0]).build();
                }).collect(Collectors.toList());
        mainCategoryRepository.saveAll(mainCategoryList);
    }

    @Transactional
    public void updateMainCategories() {
        List<MainCategory> mainCategories = mainCategoryRepository.findAll();
        mainCategories.forEach(main -> main.translateMainCategory(hm.get(main.getMainCategoryEng())));
        mainCategoryRepository.saveAll(mainCategories);
    }

    @Transactional
    public void insertSubCategories() {
        List<Category> categoryList = Arrays.stream(categories)
                .map(cate -> {
                    int index = cate.indexOf("/", 1);
                    String sub = cate.replaceAll("/", "");
                    String main = sub;
                    if (index != -1) main = cate.substring(1, index);
                    MainCategory mainCategory = mainCategoryRepository.findMainCategoryByMainCategoryEng(main).get();
                    Category newCategory = Category.builder().category(sub).build();
                    newCategory.syncMainCategory(mainCategory);
                    return newCategory;
                })
                .collect(Collectors.toList());
        categoryRepository.saveAll(categoryList);
    }

    // 전체 카테고리 조회
    public List<CategoryResponse> getCategories() {
        return mainCategoryRepository.findAll().stream()
                .map(main -> CategoryResponse.builder()
                        .categoryId(main.getId())
                        .category(main.getMainCategoryKor())
                        .build())
                .collect(Collectors.toList());
    }

    // 카테고리 검색
    public List<CategoryResponse> searchCategories(String category) {
        if (category == null || category.isBlank()) throw new Exception400("search", "검색어를 입력해주세요");
        return mainCategoryRepository.findAll().stream()
                .filter(main -> main.getMainCategoryKor().contains(category))
                .map(main -> CategoryResponse.builder()
                        .categoryId(main.getId())
                        .category(main.getMainCategoryKor())
                        .build())
                .collect(Collectors.toList());
    }

    // 관심 카테고리 조회
    public List<CategoryResponse> getLikeCategories(User user) {
        return bookmarkCategoryRepository.findBookmarkCategoriesByUser(user.getId()).stream()
                .map(category -> CategoryResponse.builder()
                        .categoryId(category.getId())
                        .category(category.getMainCategory().getMainCategoryKor())
                        .build())
                .collect(Collectors.toList());
    }

    // 관심 카테고리 생성
    @Transactional
    public void saveLikeCategory(Long userId, Long id) {
        User userPS = userRepository.findById(userId)
                .orElseThrow(() -> new Exception400("Bad-Request", "잘못된 userID입니다."));
        MainCategory mainCategory = mainCategoryRepository.findById(id)
                .orElseThrow(() -> new Exception400("Bad-Request", "해당 Category가 존재하지 않습니다."));
        BookmarkCategory bookmarkCategoryPS = bookmarkCategoryRepository.findBookmarkCategoryByMainCategory(id, userId);
        if (bookmarkCategoryPS != null) { // bookmarkCategory가 이미 존재하는 지 체크
            if (!bookmarkCategoryPS.isDeleted()) { // 삭제되지 않은 category가 존재하다면 throw
                throw new Exception400("Bad-Request", "해당 Category는 이미 등록되어 있습니다.");
            } else { // 삭제 된 category라면 undelete
                bookmarkCategoryPS.unDeleteBookmark();
            }
        } else {
            BookmarkCategory bookmarkCategory = BookmarkCategory.builder()
                    .user(userPS)
                    .mainCategory(mainCategory)
                    .build();
            bookmarkCategoryRepository.save(bookmarkCategory);
        }

    }

    @Transactional
    public void deleteLikeCategory(Long id) {
        BookmarkCategory bookmarkCategory = bookmarkCategoryRepository.findById(id)
                .orElseThrow(() -> new Exception400("Bad-Request", "해당 Category가 존재하지 않습니다."));
        bookmarkCategory.deleteBookmark();
    }
}

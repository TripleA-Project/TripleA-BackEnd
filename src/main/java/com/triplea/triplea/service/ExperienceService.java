package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.experience.ExperienceRequest;
import com.triplea.triplea.model.experience.Experience;
import com.triplea.triplea.model.experience.ExperienceRepository;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExperienceService {
    private final UserRepository userRepository;
    private final ExperienceRepository experienceRepository;

    @Transactional
    public void insertExperience(ExperienceRequest.Insert insert){
        User userPS = userRepository.findById(insert.getId()).orElseThrow(()->new Exception500("해당 ID를 가진 유저를 찾을 수 없습니다."));
        Experience experiencePS = findExperienceByUserId(userPS.getId());
        if(experiencePS != null) throw new Exception500("이미 무료체험을 받은 적이 있는 회원입니다.");
        try{

            Experience experience = new Experience(userPS, insert.getEndDate());
            experienceRepository.save(experience);
        }catch (Exception e){
            throw new Exception500("무료체험 등록 중 오류가 생겼습니다.");
        }
    }


    @Transactional
    public void updateExperience(ExperienceRequest.Update update){
        User userPS = userRepository.findById(update.getId()).orElseThrow(()->new Exception500("해당 ID를 가진 유저를 찾을 수 없습니다."));
        Experience experiencePS = findExperienceByUserId(userPS.getId());
        if(experiencePS == null) throw new Exception500("무료체험 중인 회원이 아닙니다.");
        try{
            experiencePS.updateEndDate(update.getEndDate());
        }catch (Exception e){
            throw new Exception500("무료체험 수정 중 오류가 생겼습니다.");
        }
    }

    @Transactional
    public void deleteExperience(Long id){
        User userPS = userRepository.findById(id).orElseThrow(()->new Exception500("해당 ID를 가진 유저를 찾을 수 없습니다."));
        Experience experiencePS = findExperienceByUserId(userPS.getId());
        if(experiencePS == null) throw new Exception500("무료체험 중인 회원이 아닙니다.");
        try{
            experienceRepository.deleteById(id);
        }catch (Exception e){
            throw new Exception500("무료체험 삭제 중 오류가 생겼습니다.");
        }
    }

    public boolean isUserInFreeExperiencePeriod(Long userId){

        Experience experiencePS = findExperienceByUserId(userId);
        if(experiencePS == null) return false;
        Date endDate = experiencePS.getEndDate();
        Date currentDate = new Date();

        return currentDate.compareTo(endDate) < 0;
    }

    private Experience findExperienceByUserId(Long userId) {
        return experienceRepository.findByUser(userId);
    }
}

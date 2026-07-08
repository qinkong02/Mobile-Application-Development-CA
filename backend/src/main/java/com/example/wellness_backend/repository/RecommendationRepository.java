package com.example.wellness_backend.repository;
import com.example.wellness_backend.entity.Recommendation;
import com.example.wellness_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecommendationRepository  extends JpaRepository<Recommendation, Long> {

    //时间降序查找用户所有建议记录
    List<Recommendation> findByUserOrderByGeneratedAtDesc(User user);

    //最近一条记录
    Recommendation findTopByUserOrderByGeneratedAtDesc(User user);

}

package com.example.wellness_backend.repository;


import com.example.wellness_backend.entity.WellnessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface WellnessRecordRepository extends JpaRepository<WellnessLog, Long> {

    // 查询某个用户在指定日期之后的记录
    List<WellnessLog> findByUserAndRecordDateAfter(User user, LocalDate date);

    // 查询某个用户在指定日期范围内的记录
    List<WellnessLog> findByUserAndRecordDateBetween(User user, LocalDate startDate, LocalDate endDate);

    // 查询某个用户最近N天的记录（按日期降序）
    List<WellnessLog> findTop7ByUserOrderByRecordDateDesc(User user);
}

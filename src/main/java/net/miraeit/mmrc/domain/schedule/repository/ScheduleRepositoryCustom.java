package net.miraeit.mmrc.domain.schedule.repository;

import net.miraeit.mmrc.domain.schedule.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepositoryCustom {

	List<Schedule> findScheduleOfMonth(LocalDateTime previousDate,LocalDateTime nextMonth);

	List<Schedule> findScheduleByTime(LocalDateTime startTime, LocalDateTime endTime);
}

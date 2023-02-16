package net.miraeit.mmrc.domain.schedule.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import net.miraeit.mmrc.domain.schedule.entity.Schedule;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static net.miraeit.mmrc.domain.schedule.entity.QSchedule.schedule;

public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

	private final JPAQueryFactory queryFactory; // EntityManager를 통해서 질의가 처리되고, JPQL을 사용

	public ScheduleRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<Schedule> findScheduleOfMonth(LocalDateTime previousDate, LocalDateTime nextMonth) {
		return queryFactory
				.selectFrom(schedule)
				.where(schedule.endTime.gt(previousDate),
						schedule.startTime.lt(nextMonth))
				.fetch();
	}

	@Override
	public List<Schedule> findScheduleByTime(LocalDateTime startTime, LocalDateTime endTime) {
		return queryFactory
				.selectFrom(schedule)
				.where((schedule.startTime.goe(startTime).and(schedule.startTime.lt(endTime)))
					.or( schedule.endTime.gt(startTime).and(schedule.endTime.loe(endTime))))
				.fetch();
				// where ( start_time >= startTime and start_time < endTIme )
				//    or ( end_time > startTime and end_time <= endTime )
	}
}

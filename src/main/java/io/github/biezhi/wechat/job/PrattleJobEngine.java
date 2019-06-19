package io.github.biezhi.wechat.job;

import io.github.biezhi.wechat.MyBot;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * @author ybd
 * @date 19-6-19
 * @contact yangbingdong1994@gmail.com
 */
public class PrattleJobEngine {

	public static void fire(MyBot myBot) {
		try {
			JobDetail jobDetail = JobBuilder.newJob(PrattleJob.class)
											.withIdentity(PrattleJobEngine.class.getSimpleName())
											.build();
			jobDetail.getJobDataMap().put(MyBot.class.getSimpleName(), myBot);
			CronTrigger trigger = TriggerBuilder.newTrigger()
												.withIdentity(PrattleJobEngine.class.getSimpleName())
												.withSchedule(cronSchedule(myBot.customConfig().getLoverPrattle().getCron()))
												.forJob(jobDetail)
												.build();

			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();

			scheduler.scheduleJob(jobDetail, trigger);

			scheduler.start();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	@Slf4j
	public static class PrattleJob implements Job {
		public void execute(JobExecutionContext context) {
			try {
				log.info("Send prattle job start......");
				JobDataMap dataMap = context.getJobDetail().getJobDataMap();
				MyBot myBot = (MyBot) dataMap.get(MyBot.class.getSimpleName());
				myBot.sendPrattle();
				log.info("Send prattle job end......");
			} catch (Exception e) {
				log.error("PrattleJob error: ", e);
			}
		}
	}
}

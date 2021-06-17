package gabia.cronMonitoring.repositoryImpl;

import gabia.cronMonitoring.entity.CronJob;
import gabia.cronMonitoring.entity.CronServer;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
class CronJobRepositoryImplTest {


    @Autowired
    CronJobRepositoryImpl cronJobRepository;

    @PersistenceContext
    EntityManager em;


    @Test
    @Transactional
        //@Rollback(false)
    void save() {
        //given
        CronServer cronServer = createCronServer("192.168.0.1");
        UUID uuid = UUID.randomUUID();
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());

        //when
        em.persist(cronServer);
        cronJobRepository.save(cronJob);

        //then
        em.flush();
        em.clear();

        CronJob findCronJob = em.find(CronJob.class, uuid);
        Assertions.assertThat(findCronJob).isNotNull();
        Assertions.assertThat(findCronJob.getId()).isEqualTo(uuid);
        Assertions.assertThat(findCronJob.getId()).isNotEqualByComparingTo(UUID.randomUUID());
    }

    @Test
    @Transactional
        // @Rollback(false)
    void findOne() {
        //given
        CronServer cronServer = createCronServer("192.168.0.1");
        UUID uuid = UUID.randomUUID();
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());

        //when
        em.persist(cronServer);
        UUID savedID = cronJobRepository.save(cronJob).get().getId();
        CronJob foundedCronJob = cronJobRepository.findOne(savedID).get();

        //then
        em.flush();
        em.clear();

        Assertions.assertThat(cronJob.getId()).isEqualTo(savedID);
        Assertions.assertThat(foundedCronJob.getCronExpr()).isEqualTo(cronJob.getCronExpr());
    }

    @Test
    @Transactional
    //@Rollback(false)
    void findByServer() {
        //given
        ArrayList<CronServer> cronServers = new ArrayList<>();
        int serverNum=3;
        for(int i=0;i<serverNum ;i++){
            cronServers.add(createCronServer("192.168.0.1"+i));
        }

        LinkedList<CronJob> cronJobList = new LinkedList<>();
        int jobNum = 6;
        for (int i = 0; i < jobNum; i++) {
            UUID uuid = UUID.randomUUID();
            CronJob cronJob = createCronJob(uuid, "expr" + i, "name" + i,
                cronServers.get(i%serverNum),
                new Date(), new Date());
            cronJobList.add(cronJob);
        }

        //when
        cronServers.stream().forEach(o->{
            em.persist(o);
        });
        cronJobList.stream().forEach(o -> {
            cronJobRepository.save(o);
        });

        //then
        em.flush();
        em.clear();


        for(int i = 0 ; i < serverNum;i++ ){
            CronServer tempCronServer = cronServers.get(i);
            List<CronJob> foundedCronJobList = cronJobRepository.findByServer(tempCronServer.getIp());
            org.junit.jupiter.api.Assertions.assertFalse(foundedCronJobList.isEmpty());
            foundedCronJobList.stream().forEach(o->{
                Assertions.assertThat(o.getServer().getIp()).isEqualTo(tempCronServer.getIp());
            });
        }

    }


    @Test
    @Transactional
        //@Rollback(false)
    void findAll() {
        //given
        CronServer cronServer = createCronServer("192.168.0.1");
        LinkedList<CronJob> cronJobList = new LinkedList<>();
        int num = 3;
        for (int i = 0; i < num; i++) {
            UUID uuid = UUID.randomUUID();
            CronJob cronJob = createCronJob(uuid, "expr" + i, "name" + i, cronServer,
                new Date(), new Date());
            cronJobList.add(cronJob);
        }

        //when
        em.persist(cronServer);
        cronJobList.stream().forEach(o -> {
            cronJobRepository.save(o);
        });

        //then
        em.flush();
        em.clear();

        List<CronJob> foundedCronJobList = cronJobRepository.findAll();
        org.junit.jupiter.api.Assertions.assertFalse(foundedCronJobList.isEmpty());
        Assertions.assertThat(foundedCronJobList.size()).isEqualTo(num);

    }

    @Test
    @Transactional
        //@Rollback(false)
    void deleteById() {

        //given

        CronServer cronServer = createCronServer("192.168.0.1");
        UUID uuid = UUID.randomUUID();
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());

        //when
        em.persist(cronServer);
        em.persist(cronJob);
        UUID returnedUUID = cronJobRepository.deleteById(uuid).get();

        //then
        Assertions.assertThat(returnedUUID).isEqualByComparingTo(uuid);
        Assertions.assertThat(cronJobRepository.findOne(returnedUUID).isPresent()).isFalse();

    }

    @Test
    @Transactional
        //@Rollback(false)
    void delete() {

        //given

        CronServer cronServer = createCronServer("192.168.0.1");
        UUID uuid = UUID.randomUUID();
        CronJob cronJob = createCronJob(uuid, "* * * * * test1.sh", "test1.sh", cronServer,
            new Date(), new Date());

        //when
        em.persist(cronServer);
        em.persist(cronJob);
        UUID returnedUUID = cronJobRepository.delete(cronJob).get();

        //then
        Assertions.assertThat(returnedUUID).isEqualByComparingTo(uuid);
        Assertions.assertThat(cronJobRepository.findOne(returnedUUID).isPresent()).isFalse();

    }

    public CronServer createCronServer(String ip) {
        CronServer cronServer = new CronServer();
        cronServer.setIp(ip);
        return cronServer;
    }

    public CronJob createCronJob(UUID uuid, String expr, String name, CronServer cronServer,
        Date maxEndTime, Date minStartTime) {

        CronJob cronJob = new CronJob();
        cronJob.setId(uuid);
        cronJob.setCronExpr("* * * * * test1.sh");
        cronJob.setCronName("test1.sh");
        cronJob.setServer(cronServer);
        cronJob.setMaxEndTime(new Date());
        cronJob.setMinStartTime(new Date());

        return cronJob;
    }
}
package com.crofle.livecrowdfunding;

import com.crofle.livecrowdfunding.domain.enums.PaymentMethod;
import com.crofle.livecrowdfunding.domain.enums.ProjectStatus;
import com.crofle.livecrowdfunding.domain.id.EventLogId;
import com.crofle.livecrowdfunding.domain.id.LikedId;
import com.crofle.livecrowdfunding.domain.id.UserCategoryId;
import com.crofle.livecrowdfunding.domain.entity.*;
import com.crofle.livecrowdfunding.domain.enums.UserStatus;
import com.crofle.livecrowdfunding.repository.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Log4j2
@SpringBootTest
@Rollback(false)
@Transactional
public class TestEntity {

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatePlanRepository ratePlanRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private EssentialDocumentRepository essentialDocumentRepository;

    @Autowired
    private LikedRepository likedRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private TopFundingRepository topFundingRepository;

    @Autowired
    private RevenueRepository revenueRepository;

    @Autowired
    private UserInterestRepository userInterestRepository;

    @Autowired
    private ChatReportRepository chatReportRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Test
    public void categoryEntityTest() {
        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            log.info(category);
        }
    }

    @Test
    @Transactional
    void checkRelationshipUsingRepository() {
        Category category = categoryRepository.findById(1L).orElseThrow();

        category.getProjects().forEach(project -> {
            log.info(project);
        });
    }

    @Test
    @DisplayName("UserInterest 생성 및 연관관계 매핑 테스트")
    void createAndCheckRelationship() {
        // Given
        User user = User.builder().name("테스트유저002").nickname("테스트닉네임").phone("010-1234-5678").gender(true).birth("19900101").email("test8@test.com").password("Test123!@").zipcode(12345).address("서울시 강남구").detailAddress("테헤란로 123").loginMethod(true).status(UserStatus.활성화).notification(true).registeredAt(LocalDateTime.now()).build();
        userRepository.save(user);

        Category category = categoryRepository.findById(1L).orElseThrow();

        UserCategoryId id = new UserCategoryId(user.getId(), category.getId());
        UserInterest userInterest = UserInterest.builder().id(id).user(user).category(category).build();

        // When
        UserInterest savedUserInterest = userInterestRepository.save(userInterest);

        log.info("UserInterest: " + savedUserInterest);
        // Then
//        assertThat(savedUserInterest.getId()).isEqualTo(id);
//        assertThat(savedUserInterest.getUser()).isEqualTo(user);
//        assertThat(savedUserInterest.getCategory()).isEqualTo(category);
    }

    @Test
    public void userEntityTest() {
        User user = User.builder().name("테스트유저331").nickname("테스트닉네임").phone("010-1234-5678").gender(true).birth("19900101").email("test5@test.com").password("Test123!@").zipcode(12345).address("서울시 강남구").detailAddress("테헤란로 123").loginMethod(true).status(UserStatus.활성화).notification(true).registeredAt(LocalDateTime.now()).build();
    }

    @Test
    public void projectTest() {

        //Maker
        Maker maker = Maker.builder().name("yejina").phone("010-1234-5678").business(12318282L).email("test4@test.com").password("Test123!@").zipcode(12345).address("서울시 강남구").detailAddress("테헤란로 123").registeredAt(LocalDateTime.now()).status(UserStatus.활성화).build();
        Maker savedMaker = makerRepository.save(maker);
        Maker foundMaker = makerRepository.findById(savedMaker.getId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Saved Maker ID: " + savedMaker.getId());
        System.out.println("Saved Maker Email: " + foundMaker.getEmail());

        //Manager
        Manager manager = Manager.builder().name("Kimbab").email("kimbab@gmail.com").password("12345").identificationNumber("12345678910111213141").phone("010-1234-5678").registeredAt(LocalDateTime.now()).build();
        Manager savedManager = managerRepository.save(manager);
        Manager foundManager = managerRepository.findById(savedManager.getId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Saved Manager ID: " + savedManager.getId());
        System.out.println("Saved Manager Email: " + foundManager.getEmail());


        //RatePlan
        RatePlan ratePlanAdd = RatePlan.builder().planName("Ultimate").charge((short) 1).build();
        RatePlan savePlan = ratePlanRepository.save(ratePlanAdd);
        RatePlan findPlan = ratePlanRepository.findById(savePlan.getId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("요금제 타입: " + savePlan.getId());
        System.out.println("요금제명: " + findPlan.getPlanName() + " price: " + findPlan.getCharge());


        //Category 임의로 넣기
        Category category = Category.builder().classification("전자기기").build();
        Category saveCategory = categoryRepository.save(category);
        Category findCategory = categoryRepository.findById(saveCategory.getId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Category Id: " + saveCategory.getId());
        System.out.println("category type: " + findCategory.getClassification());

        //Project
        Project project = Project.builder().maker(savedMaker).manager(savedManager).ratePlan(savePlan).category(saveCategory).productName("히터양말").summary("겨울필수템").price(200000).discountPercentage(10).startAt(LocalDateTime.now()).endAt(LocalDateTime.now().plusMonths(1)).percentage(5).reviewProjectStatus(ProjectStatus.승인).progressProjectStatus(ProjectStatus.펀딩중).goalAmount(10000000).contentImage("http://hereshouldbecontentimage").build();
        Project saveProject = projectRepository.save(project);
        Project findProject = projectRepository.findById(saveProject.getId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Project Id: " + saveProject.getId());
        System.out.println("Project 이름: " + findProject.getProductName() + " Project price: " + findProject.getPrice() + "Project 펀딩률% " + findProject.getPercentage());


        //EssentialDocument
        EssentialDocument doc = EssentialDocument.builder().project(saveProject).url("https://wheredocissaved").build();
        EssentialDocument savedDoc = essentialDocumentRepository.save(doc);
        EssentialDocument findDoc = essentialDocumentRepository.findById(savedDoc.getId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Doc Id: " + savedDoc.getId());
        System.out.println("Doc projectID: " + findDoc.getProject().getId() + " Doc loc: " + findDoc.getUrl());


        //Image
        Image image = Image.builder().project(saveProject).url("https://whereimageissaved").build();
        Image savedImage = imageRepository.save(image);
        Image findImage = imageRepository.findById(savedImage.getId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Image Id: " + savedImage.getId());
        System.out.println("Image projectID: " + findImage.getProject().getId() + " Image loc: " + findImage.getUrl());

        //TopFunding
        TopFunding topFunding = TopFunding.builder().project(saveProject).ranking(1).updatedAt(LocalDateTime.now()).build();
        TopFunding savedFunding = topFundingRepository.save(topFunding);
        TopFunding findFunding = topFundingRepository.findById(savedFunding.getId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Topfunding Id: " + savedFunding.getId());
        System.out.println("Topfunding projectID: " + findFunding.getProject().getId() + " TopFunding Ranking: " + findFunding.getRanking());


        //Order
        User user = User.builder().name("hojin").nickname("hojingogo").phone("010-1234-5678").gender(true).birth("19900101").email("test12@test.com").password("Test123!@").zipcode(12345).address("서울시 강남구").detailAddress("테헤란로 123").loginMethod(true).status(UserStatus.활성화).notification(true).registeredAt(LocalDateTime.now()).build();
        User savedUser = userRepository.save(user);

        Orders order = Orders.builder().user(savedUser).project(saveProject).amount(5).paymentPrice(100000).build();
        Orders savedOrder = ordersRepository.save(order);
        Orders findOrder = ordersRepository.findById(savedOrder.getId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Order Id: " + savedOrder.getId());
        System.out.println("Order UserId: " + findOrder.getUser().getId() + " Order ProjectId: " + findOrder.getProject().getId() + " amount, price: " + findOrder.getAmount() + findOrder.getPaymentPrice());

        //PaymentHistory
        PaymentHistory payHistory = PaymentHistory.builder().order(savedOrder).paymentMethod(PaymentMethod.카드).deliveryAddress("서울 강남구").paymentAt(LocalDateTime.now()).refundStatus(0).build();
        PaymentHistory savedPayment = paymentHistoryRepository.save(payHistory);
        PaymentHistory findPayment = paymentHistoryRepository.findById(savedPayment.getOrderId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Payment Id: " + savedPayment.getOrder().getId());
        System.out.println("Payment method: " + findPayment.getPaymentMethod() + " Payment date: " + findPayment.getPaymentAt() + " Payment delivery address: " + findPayment.getDeliveryAddress());


        //Liked
        LikedId id = new LikedId(savedUser.getId(), saveProject.getId());
        Liked liked = Liked.builder().id(id).user(savedUser).project(saveProject).createdAt(LocalDateTime.now()).build();
        Liked savedLiked = likedRepository.save(liked);
//        Liked findLiked = likedRepository.findById(savedLiked.getId());
        System.out.println("Liked Id: " + savedLiked.getId());

        //Revenue
        Revenue revenue = Revenue.builder().project(saveProject).totalAmount(1000000).build();
        Revenue savedRevenue = revenueRepository.save(revenue);
        Revenue findRevenue = revenueRepository.findById(savedRevenue.getProjectId()).orElseThrow(() -> new RuntimeException("Not present"));
        System.out.println("Revenue Id: " + savedRevenue.getProjectId());
        System.out.println("Revenue Project: " + findRevenue.getProject().getId() + " Revenue Total amount: " + findRevenue.getTotalAmount());

    }


    @Test
    public void FindUserTest() {
        User user = userRepository.findById(1L).orElseThrow();
        log.info(user);
    }

    @Test
    public void FindManagerTest() {
        Manager manager = managerRepository.findById(1L).orElseThrow();
        log.info(manager);
    }

    @Test
    public void FindChatReportTest() {
        ChatReport chatReport = chatReportRepository.findById(1L).orElseThrow();
        log.info(chatReport);
    }

    @Test
    public void SaveChatReportTest() {
        User user = userRepository.findById(3L).orElseThrow();
        Project project = Project.builder().id(2L).build();
        Manager manager = managerRepository.findById(2L).orElseThrow();
        ChatReport chatReport = ChatReport.builder().user(user).project(project).manager(manager).reason("부적절한 채팅 내용1").chatMessage("안녕하세요").createdAt(LocalDateTime.now()).build();
        ChatReport savedChatReport = chatReportRepository.save(chatReport);
        log.info(savedChatReport);
    }

    @Test
    public void FindMakerTest() {
        Maker maker = makerRepository.findById(1L).orElseThrow();
        log.info(maker);
    }

    @Test
    public void SaveMakerTest() {
        Maker maker = Maker.builder().name("테스트메이커").phone("010-1234-5678").business(123456L).email("aa@.com").password("1234").zipcode(12345).address("서울시 강남구").detailAddress("테헤란로 123").unregisteredAt(LocalDateTime.now()).registeredAt(LocalDateTime.now()).status(UserStatus.활성화).build();
        Maker savedMaker = makerRepository.save(maker);
        log.info(savedMaker);
    }

    @Test
    public void FindScheduleTest() {
        Schedule schedule = scheduleRepository.findById(1L).orElseThrow();
        log.info(schedule);
    }

    @Test
    public void SaveScheduleTest() {
        Project project = Project.builder().id(1L).build();
        Schedule schedule = Schedule.builder().project(project).date(LocalDateTime.now()).build();
        Schedule savedSchedule = scheduleRepository.save(schedule);
        log.info(savedSchedule);
    }

    @Test
    public void FindScriptTest() {
        Script script = scriptRepository.findById(1L).orElseThrow();
        log.info(script);
    }

    @Test
    public void SaveScriptTest() {
        Schedule schedule = Schedule.builder().id(1L).build();
        Script script = Script.builder().schedule(schedule).scriptUrl("http://test.com").build();
        Script savedScript = scriptRepository.save(script);
        log.info(savedScript);
    }

    @Test
    public void FindVideoTest() {
        Video video = videoRepository.findById(1L).orElseThrow();
        log.info(video);
    }

    @Test
    public void SaveVideoTest() {
        Schedule schedule = Schedule.builder().id(1L).build();
        Video video = Video.builder().schedule(schedule).mediaUrl("http://test.com").build();
        Video savedVideo = videoRepository.save(video);
        log.info(savedVideo);
    }

    @Test
    public void FindEventTest() {
        Event event = eventRepository.findById(1L).orElseThrow();
        log.info(event);
    }

    @Test
    public void SaveEventTest() {
        Project project = Project.builder().id(1L).build();
        Event event = Event.builder().project(project).name("테스트이벤트").content("테스트이벤트내용").prize("테스트상품").maxParticipants(100).createdAt(LocalDateTime.now()).build();
        Event savedEvent = eventRepository.save(event);
        log.info(savedEvent);
    }

    @Test
    public void FindEventLogTest() {
        EventLog eventLog = EventLog.builder().id(new EventLogId(1L, 1L)).event(Event.builder().id(1L).build()).user(User.builder().id(1L).build()).winningPrize("테스트상품").winningDate(LocalDateTime.now()).createdAt(LocalDateTime.now()).build();

        EventLog savedEventLog = eventLogRepository.save(eventLog);
        log.info(savedEventLog);

        List<EventLog> eventLogs = eventLogRepository.findAll();
        for (EventLog eventLog1 : eventLogs) {
            log.info(eventLog1);
        }
    }
}
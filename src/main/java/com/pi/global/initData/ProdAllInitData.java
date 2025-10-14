//package com.pi.global.initData;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//@Configuration
//@Profile("prod")
//@RequiredArgsConstructor
//public class ProdAllInitData {
//    private final ProdAnswerInitData prodAnswerInitData;
//    private final ProdApplicationInitData prodApplicationInitData;
//    private final ProdCategoryInitData prodCategoryInitData;
//    private final ProdFreelancerInitData prodFreelancerInitData;
//    private final ProdOfferInitData prodOfferInitData;
//    private final ProdProjectInitData prodProjectInitData;
//    private final ProdQuestionInitData prodQuestionInitData;
//    private final ProdRegionInitData prodRegionInitData;
//    private final ProdReportInitData prodReportInitData;
//    private final ProdReviewInitData prodReviewInitData;
//    private final ProdSkillInitData prodSkillInitData;
//    private final ProdUserInitData prodUserInitData;
//
//    @Bean
//    ApplicationRunner initDataRunner() {
//        return args -> {
//            prodUserInitData.initUsers();
//            prodQuestionInitData.initQuestions();
//            prodAnswerInitData.initAnswers();
//            prodRegionInitData.initRegions();
//            prodCategoryInitData.initCategories();
//            prodSkillInitData.initSkills();
//            prodProjectInitData.initProjects();
//            prodApplicationInitData.initApplications();
//            prodFreelancerInitData.initFreelancers();
//            prodOfferInitData.initOffers();
//            prodReviewInitData.initReviews();
//            prodReportInitData.initReports();
//        };
//    }
//
//}

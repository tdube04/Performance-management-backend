package com.innovation.workplan.Services;

import com.innovation.workplan.CollectionModels.Scorecard;
import com.innovation.workplan.CollectionModels.Workplan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.Dictionary;
import java.util.List;

public interface WorkPlanService {
    long save(Workplan workplan);

    Page<Workplan> searchPlan(Long id, String period, String name, Pageable pageable);

    Workplan searchUserPlan( String User_email, String period,String planStatus);


    List<Workplan> searchPlanByEvaluator(String evaluator_email, String period, String planStatus);

    List<Workplan> searchPlanByStatus(String planStatus);



    Workplan getMyWorkPlan(String period);

    String approveWorkplan(Long id);

    String disApproveWorkplan(Long id,String  message);

    Workplan deleteIndicator(Long id, String email, String area,String program,String indicator);





    //void getDocument(HttpServletResponse response) throws IOException, JRException;

    Workplan update(Workplan workplan);

    Workplan exportWorkplanPDF(Principal principal);
}

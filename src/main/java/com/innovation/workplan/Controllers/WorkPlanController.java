package com.innovation.workplan.Controllers;

import com.innovation.workplan.CollectionModels.*;
import com.innovation.workplan.Repositories.WorkPlanRepository;
import com.innovation.workplan.Services.WorkPlanService;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
//import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
//import net.sf.jasperreports.export.SimpleExporterInput;
//import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
//import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/workplan")
public class WorkPlanController {

   @Autowired
   private WorkPlanService workPlanService;

   @Autowired
   private MongoTemplate mongoTemplate;

   @Autowired
   private WorkPlanRepository workPlanRepository;


   @PostMapping(value= "/save")
   @PreAuthorize("hasAnyAuthority('SAVE_WORKPLAN')")
   @Operation(summary = "saving a Workplan in the database")
   public long savePlan(@RequestBody Workplan workplan){
      return workPlanService.save(workplan);
   }

   @GetMapping("/searchWorkplan")
   @PreAuthorize("hasAnyAuthority('SEARCH_WORKPLAN')")
   @Operation(summary = "searching a Workplan in the database")
   public Page<Workplan> searchWorkPlan(
           @RequestParam (required=false) Long id,
           @RequestParam (required=false) String username,
           @RequestParam (required=false) String period,

           @RequestParam (defaultValue="0") Integer page,
           @RequestParam (defaultValue="5") Integer size){
      Pageable pageable=PageRequest.of(page,size);
      return workPlanService.searchPlan(id,username,period,pageable);
   }
   @RequestMapping(value = "/WorkplanReport", method = RequestMethod.GET)
   @ResponseBody
   @PreAuthorize("hasAnyAuthority('SHOW_PDF')")
   public Workplan showWorkplanPDF(Principal principal){
      return workPlanService.exportWorkplanPDF(principal);
   }


   @PutMapping("/updateWorkplan/{id}")
   @PreAuthorize("hasAnyAuthority('UPDATE_WORKPLAN')")
   @Operation(summary = "updating a Workplan in the database")
   public Workplan updateWorkplan(@PathVariable Long id, @RequestBody Workplan workplan){
      workplan.setId(id);
      return this.workPlanService.update(workplan);
//      return "Successfully Updated workplan with id"+"\n"+id;
   }

   @GetMapping("/disapproveWorkplan/{id}")
   @PreAuthorize("hasAnyAuthority('DISAPPROVE_WORKPLAN')")
   @Operation(summary = "Disapproving a Workplan ")
   public String disApproveWorkplan(@RequestParam (required=true) Long id,@RequestParam (required=true) String message){
      return (this.workPlanService.disApproveWorkplan(id,message));
   }
   @GetMapping("/approveWorkplan/{id}")
   @PreAuthorize("hasAnyAuthority('APPROVE_WORKPLAN')")
   @Operation(summary = "Approving a Workplan ")
   public String ApproveWorkplan(@RequestParam (required=true) Long id){
      return (this.workPlanService.approveWorkplan(id));
   }

   @GetMapping("/getMyWorkplan")
   @PreAuthorize("hasAnyAuthority('SEARCH_WORKPLAN_BY_APPRAISER')")
   @Operation(summary = "updating a Workplan in the database")
   public Workplan GetMyWorkplan(@RequestParam (required=true) String period){
      return (this.workPlanService.getMyWorkPlan(period));
   }

   @GetMapping("/searchWorkplanByEvaluator")
   @PreAuthorize("hasAnyAuthority('SEARCH_WORKPLAN_BY_EVALUATOR')")
   @Operation(summary = "updating a Workplan in the database")
   public List<Workplan> SearchWorkplanByEvaluator(@RequestParam (required=true) String evaluator_email,
                                             @RequestParam (required=true) String period,
                                             @RequestParam (required=true) String planStatus){

      return (this.workPlanService.searchPlanByEvaluator(evaluator_email,period,planStatus));
   }

   @GetMapping("/searchWorkplanByStatus")
   @PreAuthorize("hasAnyAuthority('SEARCH_WORKPLAN_BY_STATUS')")
   @Operation(summary = "searching a Workplan in the database")
   public List<Workplan> SearchWorkplanByStatus(@RequestParam (required=true) String planStatus){
      return (this.workPlanService.searchPlanByStatus(planStatus));
   }

   @GetMapping("/searchWorkplanByAppraisee")
   @PreAuthorize("hasAnyAuthority('SEARCH_WORKPLAN_BY_APPRAISEE')")
   @Operation(summary = "updating a Workplan in the database")
   public Workplan SearchWorkplanByAppraisee(@RequestParam (required=true) String User_email,
                                             @RequestParam (required=true) String period,
                                              @RequestParam (required=true)String planStatus){

      return (this.workPlanService.searchUserPlan(User_email,period,planStatus));
   }
   @GetMapping("/DeleteIndicator")
   @PreAuthorize("hasAnyAuthority('DELETE_INDICATOR')")
   @Operation(summary = "deleting an indicator  of a performance area")
   public Workplan deleteIndicator(
           @RequestParam (required=true) Long id,
           @RequestParam (required=true) String User_email,
                                             @RequestParam (required=true) String perfomanceArea,
           @RequestParam (required=true) String prog,
                                             @RequestParam (required=true)String indicatorName){
      Pageable pageable=PageRequest.of(0,1);

      return workPlanService.deleteIndicator(id,User_email,perfomanceArea,prog,indicatorName);

//      List<WorkplanPerformanceArea> areas=workplan.getAreasOfPerformnce();
//      for(WorkplanPerformanceArea wpa:areas){
//         if(wpa.getDescription()==perfomanceArea){
//            ArrayList<KPI> kpi=wpa.getPrograms();
//            for(KPI kp:kpi){
//               if(kp.getName()==prog){
//                  List<MPI> ind=kp.getIndicators();
//                  for(MPI mp:ind){
//                     if(mp.getDescription()==indicatorName){
//                        ind.remove(mp);
//                        kp.setIndicators(ind);
//                        wpa.setPrograms(kpi);
//                        workplan.setAreasOfPerformnce(areas);
//                        Query query = new Query();
//                        query.addCriteria(Criteria.where("_id").is(workplan.getId()));
//                        Update updateworkplan = new Update();
//                        updateworkplan.set("user_email", workplan.getUser_email());
//                        updateworkplan.set("evaluator_email",workplan.getEvaluator_email());
//                        updateworkplan.set("appraiser_email", workplan.getAppraiser_email());
//                        updateworkplan.set("evaluationPeriod", workplan.getEvaluationPeriod());
//
//                        updateworkplan.set("AreasOfPerformnce", workplan.getAreasOfPerformnce());
//                        updateworkplan.set("workplanStatus",workplan.getWorkplanStatus());
//                        return mongoTemplate.findAndModify(query, updateworkplan, new FindAndModifyOptions().returnNew(true), Workplan.class);
//
//                     }
//
//                  }
//
//               }
//
//            }
//         }
//
//      }




   }



}


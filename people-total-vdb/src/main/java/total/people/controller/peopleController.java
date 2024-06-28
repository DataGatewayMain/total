package total.people.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import total.people.entity.People;
import total.people.service.peopleService;

import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = {"http://localhost:4200", "http://192.168.0.5:4200"})
@RestController
public class peopleController {

    private static final Logger logger = LoggerFactory.getLogger(peopleController.class);

    @Autowired
    private peopleService peopleService;

    @Autowired
    private PagedResourcesAssembler<People> pagedResourcesAssembler;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam Map<String, String> allParams,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam String api) {

        logger.info("Received search request with params: {}, page: {}, size: {}, api: {}", allParams, page, size, api);

        // Filter out parameters that are not in the People entity
        Map<String, String> validParams = new HashMap<>();
        allParams.forEach((key, value) -> {
            if ((key.startsWith("include_") || key.startsWith("exclude_")) && !value.isEmpty()) {
                validParams.put(key, value);
            }
        });

        // Convert 1-based page index to 0-based page index
        int zeroBasedPage = page > 0 ? page - 1 : 0;

        logger.info("Valid parameters: {}, zero-based page: {}", validParams, zeroBasedPage);

        Page<People> peoplePage;

        if (validParams.isEmpty()) {
            long totalCount = peopleService.count();
            
            Map<String, Object> response = new HashMap<>();
            response.put("total_count", totalCount);

            return ResponseEntity.ok(response);
        } else {
            peoplePage = peopleService.searchProspects(validParams, zeroBasedPage, size);

            PagedModel<EntityModel<People>> pagedModel = pagedResourcesAssembler.toModel(peoplePage);

            Map<String, Object> response = new HashMap<>();
            response.put("total_data", pagedModel.getContent());
            response.put("pagination_total", generatePaginationInfo(peoplePage));
            response.put("total_count", peoplePage.getTotalElements());

            return ResponseEntity.ok(response);
        }
    }

    private Map<String, String> generatePaginationInfo(Page<People> peoplePage) {
        Map<String, String> paginationInfo = new HashMap<>();
        paginationInfo.put("current_page_total", String.valueOf(peoplePage.getNumber() + 1));
        paginationInfo.put("total_pages_total", String.valueOf(peoplePage.getTotalPages()));
        paginationInfo.put("records_per_page_total", String.valueOf(peoplePage.getSize()));
        return paginationInfo;
    }
}

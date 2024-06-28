package total.people.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import total.people.config.RedisCache;
import total.people.entity.People;
import total.people.repo.peopleRepository;

import java.util.Map;
import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@Service
public class peopleService {

    private static final Logger logger = LoggerFactory.getLogger(peopleService.class);

    @Autowired
    private peopleRepository peopleRepository;
    
    @Autowired
    private RedisCache redisCache;

    @Cacheable(value = "people", key = "{#allParams.hashCode(), #page, #size}")
    public Page<People> searchProspects(Map<String, String> allParams, int page, int size) {
        logger.info("Searching prospects with params: {}, page: {}, size: {}", allParams, page, size);
        Specification<People> specification = peopleSpecification.getSpecifications(allParams);
        Pageable pageable = PageRequest.of(page, size);
        return peopleRepository.findAll(specification, pageable);
    }

    public Page<People> findAll(PageRequest of) {
        return peopleRepository.findAll(of);
    }

    public long count() {
        return peopleRepository.count();
    }
}
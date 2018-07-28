package com.margin.calculate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@SpringBootApplication
@RestController
public class CalculateApplication {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@RequestMapping("/getMarginById")
	public Map<String, Object> getMarginById(String fundId){
		String sql = "select * from positions as A \n" +
				"inner join pmc B on A.securityId = B.securityId\n" +
				"inner join smc C on B.securityId = C.securityId \n" +
				"inner join fund D on A.fundId = D.fundId\n" +
				"inner join marginconfiguration M on A.fundId=M.fundId And C.rateType = M.rateType where A.fundId = 1000;";
		List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql);
		List<Double> markValuesList = new ArrayList<>();
		double margins=0.0;
		String fundName="";
		for (Map<String, Object> map : list) {
		    fundName = map.get("name").toString();
            double markValues = (int)map.get("netPosition") * (double)map.get("price");
            markValuesList.add(markValues);
            margins += markValues*(double)map.get("rate");
		}
		markValuesList.sort(Double::compareTo);
        margins += markValuesList.get(markValuesList.size()-1)*0.2 + markValuesList.get(markValuesList.size()-2)*0.1;
        Map<String, Object> result = new HashMap<>();
        result.put("fundId",fundId);
        result.put("fundName",fundName);
        result.put("margins",margins);
		return result;
	}

	public static void main(String[] args) {
		SpringApplication.run(CalculateApplication.class, args);
	}
}

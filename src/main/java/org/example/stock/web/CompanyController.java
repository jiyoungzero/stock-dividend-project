package org.example.stock.web;

import lombok.AllArgsConstructor;
import org.example.stock.model.Company;
import org.example.stock.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("autocomplete")
    public ResponseEntity<?> autocompleteCompany(@RequestParam String keyword) {
        return null;
    }

    @GetMapping
    public ResponseEntity<?> searchCompany() {
        return null;
    }

    // 배당금 정보 저장하기
    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody Company request){
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }

        Company company = this.companyService.save(ticker);
        return ResponseEntity.ok(company);
    }

    // 배당금 정보 삭제하기
    @DeleteMapping
    public ResponseEntity<?> deleteCompany(){
        return null;
    }
}

package org.example.stock.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class CompanyController {

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
    public ResponseEntity<?> addCompany(){
        return null;
    }

    // 배당금 정보 삭제하기
    @DeleteMapping
    public ResponseEntity<?> deleteCompany(){
        return null;
    }
}

package com.nguyensao.ecommerce_layered_architecture.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.InventoryDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.InventoryRequest;
import com.nguyensao.ecommerce_layered_architecture.service.InventoryService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping(ApiPathConstant.INVENTORY_IMPORT)
    public ResponseEntity<InventoryDto> importInventory(@RequestBody InventoryRequest request) {
        InventoryDto inventoryDto = inventoryService.importInventory(request);
        return ResponseEntity.ok(inventoryDto);
    }

    @PostMapping(ApiPathConstant.INVENTORY_EXPORT)
    public ResponseEntity<InventoryDto> exportInventory(@RequestBody InventoryRequest request) {
        InventoryDto inventoryDto = inventoryService.exportInventory(request);
        return ResponseEntity.ok(inventoryDto);
    }

    @GetMapping(ApiPathConstant.INVENTORY_GET_ALL)
    public ResponseEntity<List<InventoryDto>> getAllInventories(
            @RequestParam(defaultValue = "all") String sortByStock) {
        List<InventoryDto> inventories = inventoryService.getAllInventories(sortByStock);
        return ResponseEntity.ok(inventories);
    }
}
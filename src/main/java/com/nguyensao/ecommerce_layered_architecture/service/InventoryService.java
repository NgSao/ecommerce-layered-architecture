package com.nguyensao.ecommerce_layered_architecture.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nguyensao.ecommerce_layered_architecture.constant.InventoryConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.InventoryDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.InventoryRequest;
import com.nguyensao.ecommerce_layered_architecture.event.domain.InventoryEvent;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.Inventory;
import com.nguyensao.ecommerce_layered_architecture.model.Product;
import com.nguyensao.ecommerce_layered_architecture.model.Variant;
import com.nguyensao.ecommerce_layered_architecture.repository.InventoryRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.ProductRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.VariantRepository;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository, VariantRepository variantRepository,
            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
    }

    public void createInventory(InventoryEvent event) {
        if (event.getQuantity() == null || event.getQuantity() < 0) {
            throw new AppException(InventoryConstant.INVALID_STOCK_QUANTITY);
        }
        Inventory inventory = new Inventory();
        inventory.setSkuProduct(event.getSkuProduct());
        inventory.setSkuVariant(event.getSkuVariant());
        inventory.setQuantity(event.getQuantity());
        inventory.setLastUpdated(Instant.now());
        inventoryRepository.save(inventory);
    }

    public void updateInventory(InventoryEvent event) {

        if (event.getQuantity() == null || event.getQuantity() < 0) {
            throw new AppException(InventoryConstant.INVALID_STOCK_QUANTITY);
        }
        Inventory inventory = (event.getSkuVariant() != null)
                ? inventoryRepository.findBySkuVariant(event.getSkuVariant())
                        .orElseThrow(() -> new AppException(
                                InventoryConstant.STOCK_NOT_FOUND_VARIANT + event.getSkuVariant()))
                : inventoryRepository.findBySkuProductAndSkuVariantIsNull(event.getSkuProduct())
                        .orElseThrow(() -> new AppException(
                                InventoryConstant.STOCK_NOT_FOUND_PRODUCT + event.getSkuProduct()));

        inventory.setQuantity(event.getQuantity());
        inventory.setLastUpdated(Instant.now());
        inventoryRepository.save(inventory);
    }

    public void deleteInventory(InventoryEvent event) {
        if (event.getSkuVariant() != null) {
            Inventory inventory = inventoryRepository.findBySkuVariant(event.getSkuVariant())
                    .orElseThrow(() -> new AppException(
                            InventoryConstant.STOCK_NOT_FOUND_VARIANT + event.getSkuVariant()));
            inventoryRepository.delete(inventory);
        } else {
            Inventory inventory = inventoryRepository.findBySkuProductAndSkuVariantIsNull(event.getSkuProduct())
                    .orElseThrow(() -> new AppException(
                            InventoryConstant.STOCK_NOT_FOUND_PRODUCT + event.getSkuProduct()));
            inventoryRepository.delete(inventory);
        }
    }

    public void deductInventory(InventoryEvent event) {

        if (event.getQuantity() == null || event.getQuantity() < 0) {
            throw new AppException(InventoryConstant.INVALID_DEDUCT_STOCK_QUANTITY);
        }

        Inventory inventory;
        Product product = null;
        if (event.getSkuVariant() != null) {
            Variant variant = variantRepository.findByIdAndSize(
                    Long.parseLong(event.getSkuProduct()),
                    event.getSkuVariant())
                    .orElseThrow(() -> new AppException(
                            "Biến thể sản phẩm không tồn tại với id=" + event.getSkuProduct() +
                                    " và size=" + event.getSkuVariant()));

            inventory = inventoryRepository.findBySkuVariant(variant.getSku())
                    .orElseThrow(
                            () -> new AppException(InventoryConstant.STOCK_NOT_FOUND_VARIANT + event.getSkuVariant()));
        } else {
            long skuProduct = 0;
            try {
                skuProduct = Long.parseLong(event.getSkuProduct());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            product = productRepository.findById(skuProduct)
                    .orElseThrow(() -> new AppException(
                            InventoryConstant.STOCK_NOT_FOUND_PRODUCT + event.getSkuProduct()));
            inventory = inventoryRepository.findBySkuProductAndSkuVariantIsNull(product.getSku())
                    .orElseThrow(
                            () -> new AppException(InventoryConstant.STOCK_NOT_FOUND_PRODUCT + event.getSkuProduct()));
        }

        int newQuantity = inventory.getQuantity() - event.getQuantity();
        if (newQuantity < 0) {
            throw new AppException(InventoryConstant.INSUFFICIENT_STOCK);
        }
        inventory.setQuantity(newQuantity);
        inventory.setLastUpdated(Instant.now());
        inventoryRepository.save(inventory);

    }

    public InventoryDto importInventory(InventoryRequest request) {
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new AppException(InventoryConstant.INVALID_IMPORT_STOCK_QUANTITY);
        }
        Inventory inventory;
        if (request.getSkuVariant() != null) {
            inventory = inventoryRepository.findBySkuVariant(request.getSkuVariant())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setSkuProduct(request.getSkuProduct());
                        newInventory.setSkuVariant(request.getSkuVariant());
                        newInventory.setQuantity(0);
                        return newInventory;
                    });
        } else {
            inventory = inventoryRepository.findBySkuProductAndSkuVariantIsNull(request.getSkuProduct())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setSkuProduct(request.getSkuProduct());
                        newInventory.setSkuVariant(null);
                        newInventory.setQuantity(0);
                        return newInventory;
                    });
        }
        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        inventory.setLastUpdated(Instant.now());
        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToDto(savedInventory);
    }

    public InventoryDto exportInventory(InventoryRequest request) {
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new AppException(InventoryConstant.INVALID_EXPORT_STOCK_QUANTITY);
        }
        Inventory inventory;
        if (request.getSkuVariant() != null) {
            inventory = inventoryRepository.findBySkuVariant(request.getSkuVariant())
                    .orElseThrow(() -> new RuntimeException(
                            InventoryConstant.STOCK_NOT_FOUND_VARIANT + request.getSkuVariant()));
        } else {
            inventory = inventoryRepository.findBySkuProductAndSkuVariantIsNull(request.getSkuProduct())
                    .orElseThrow(() -> new RuntimeException(
                            InventoryConstant.STOCK_NOT_FOUND_PRODUCT + request.getSkuVariant()));
        }
        int newQuantity = inventory.getQuantity() - request.getQuantity();
        if (newQuantity < 0) {
            throw new RuntimeException("Tồn kho không đủ");
        }
        inventory.setQuantity(newQuantity);
        inventory.setLastUpdated(Instant.now());
        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToDto(savedInventory);
    }

    public List<InventoryDto> getAllInventories(String sortByStock) {
        List<Inventory> inventories;
        switch (sortByStock.toLowerCase()) {
            case "instock":
                inventories = inventoryRepository.findAllByQuantityGreaterThan(0);
                break;
            case "outofstock":
                inventories = inventoryRepository.findAllByQuantityEquals(0);
                break;
            default:
                inventories = inventoryRepository.findAll();
                break;
        }
        return inventories.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private InventoryDto mapToDto(Inventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setId(inventory.getId());
        dto.setSkuProduct(inventory.getSkuProduct());
        dto.setSkuVariant(inventory.getSkuVariant());
        dto.setQuantity(inventory.getQuantity());
        dto.setLastUpdated(inventory.getLastUpdated());
        return dto;
    }
}

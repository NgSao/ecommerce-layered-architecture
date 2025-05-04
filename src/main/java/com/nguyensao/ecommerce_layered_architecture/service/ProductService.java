package com.nguyensao.ecommerce_layered_architecture.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.constant.ProductConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.ProductDto;
import com.nguyensao.ecommerce_layered_architecture.dto.VariantDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ProductRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.VariantRequest;
import com.nguyensao.ecommerce_layered_architecture.event.EventType;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.InventoryPublisher;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.mapper.ProductMapper;
import com.nguyensao.ecommerce_layered_architecture.model.Media;
import com.nguyensao.ecommerce_layered_architecture.model.Product;
import com.nguyensao.ecommerce_layered_architecture.model.Variant;
import com.nguyensao.ecommerce_layered_architecture.repository.ProductRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.VariantRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final ProductMapper productMapper;
    private final InventoryPublisher inventoryPublisher;
    private final FileService fileService;

    public ProductService(ProductRepository productRepository, VariantRepository variantRepository,
            ProductMapper productMapper, InventoryPublisher inventoryPublisher,
            FileService fileService) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.productMapper = productMapper;
        this.inventoryPublisher = inventoryPublisher;
        this.fileService = fileService;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::productToDto)
                .toList();

    }

    public ProductDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.PRODUCT_NOT_FOUND));
        return productMapper.productToDto(product);
    }

    public ProductDto createProduct(ProductRequest request) {
        Product product = productMapper.productToEntity(request);
        productRepository.save(product);
        if (product.getVariants() == null || product.getVariants().isEmpty()) {
            if (request.getStock() != null) {
                inventoryPublisher.publishInventoryEvent(EventType.CREATE_INVENTORY, product.getSku(), null,
                        request.getStock());
            }
        } else {
            for (Variant variant : product.getVariants()) {
                if (variant.getStockQuantity() != null && variant.getStockQuantity() >= 0) {
                    inventoryPublisher.publishInventoryEvent(EventType.CREATE_INVENTORY, product.getSku(),
                            variant.getSku(),
                            variant.getStockQuantity());
                } else {
                    throw new AppException("Số lượng biến thể không hợp lệ");
                }
            }
        }
        return productMapper.productToDto(product);
    }

    public ProductDto updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.PRODUCT_NOT_FOUND));
        productMapper.productUpdatedToEntity(existingProduct, request);
        Product product = productRepository.save(existingProduct);
        if (product.getVariants() == null || product.getVariants().isEmpty()) {
            if (request.getStock() != null) {
                inventoryPublisher.publishInventoryEvent(EventType.UPDATE_INVENTORY, product.getSku(), null,
                        request.getStock());
            }
        } else {
            for (Variant variant : product.getVariants()) {
                if (variant.getStockQuantity() != null) {
                    inventoryPublisher.publishInventoryEvent(EventType.UPDATE_INVENTORY, product.getSku(),
                            variant.getSku(),
                            variant.getStockQuantity());
                }
            }
        }
        return productMapper.productToDto(product);
    }

    public void deleteProduct(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.PRODUCT_NOT_FOUND));
        productRepository.delete(existingProduct);
        if (existingProduct.getVariants() == null || existingProduct.getVariants().isEmpty()) {
            inventoryPublisher.publishInventoryEvent(EventType.DELETE_INVENTORY, existingProduct.getSku(), null, 0);
        } else {
            for (Variant variant : existingProduct.getVariants()) {
                inventoryPublisher.publishInventoryEvent(EventType.DELETE_INVENTORY, existingProduct.getSku(),
                        variant.getSku(), 0);
            }
        }
    }

    public VariantDto addVariant(VariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ProductConstant.PRODUCT_NOT_FOUND));
        Variant variant = productMapper.variantToEntity(request, product);
        Variant savedVariant = variantRepository.save(variant);
        inventoryPublisher.publishInventoryEvent(EventType.CREATE_INVENTORY, product.getSku(),
                variant.getSku(),
                variant.getStockQuantity());
        return productMapper.variantToDto(savedVariant);
    }

    public VariantDto updateVariant(Long variantId, VariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ProductConstant.PRODUCT_NOT_FOUND));
        Variant existingVariant = variantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ProductConstant.VARIANT_NOT_FOUND));
        productMapper.variantUpdatedToEntity(existingVariant, request, product);

        Variant savedVariant = variantRepository.save(existingVariant);
        inventoryPublisher.publishInventoryEvent(EventType.UPDATE_INVENTORY, product.getSku(),
                savedVariant.getSku(),
                savedVariant.getStockQuantity());
        return productMapper.variantToDto(savedVariant);
    }

    public void deleteVariant(Long variantId) {
        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ProductConstant.VARIANT_NOT_FOUND));
        variantRepository.delete(variant);
        inventoryPublisher.publishInventoryEvent(EventType.DELETE_INVENTORY, variant.getProduct().getSku(),
                variant.getSku(), 0);
    }

    public ProductDto uploadProductImages(Long productId, MultipartFile[] images) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ProductConstant.PRODUCT_NOT_FOUND));
        if (images != null && images.length > 0) {
            List<String> imageUrls = fileService.uploadMultipleImages(images);
            Set<Media> mediaSet = imageUrls.stream().map(url -> {
                Media media = new Media();
                media.setProduct(product);
                media.setImageUrl(url);
                media.setDisplayOrder(0);
                media.setIsPublished(true);
                return media;
            }).collect(Collectors.toSet());
            product.getImages().addAll(mediaSet);
            productRepository.save(product);
        } else {
            throw new AppException("Danh sách ảnh không được để trống!");
        }
        return productMapper.productToDto(product);
    }

    public VariantDto uploadVariantImage(Long variantId, MultipartFile image) throws IOException {
        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ProductConstant.VARIANT_NOT_FOUND));
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.uploadImage(image);
            variant.setImageUrl(imageUrl);
            variantRepository.save(variant);
        } else {
            throw new AppException("Ảnh không được để trống!");
        }
        return productMapper.variantToDto(variant);
    }

}
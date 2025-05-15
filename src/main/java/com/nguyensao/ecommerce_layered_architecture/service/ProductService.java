package com.nguyensao.ecommerce_layered_architecture.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.constant.ProductConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.ProductDto;
import com.nguyensao.ecommerce_layered_architecture.dto.VariantDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ProductRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.VariantCreateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.AdminProductResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.ProductColorResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.ProductResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.SimplifiedPageResponse;
import com.nguyensao.ecommerce_layered_architecture.event.EventType;
import com.nguyensao.ecommerce_layered_architecture.event.domain.InventoryEvent;
import com.nguyensao.ecommerce_layered_architecture.event.domain.ProductEvent;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.InventoryPublisher;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.mapper.ProductAdminMapper;
import com.nguyensao.ecommerce_layered_architecture.mapper.ProductMapper;
import com.nguyensao.ecommerce_layered_architecture.mapper.ResponseMapper;
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
    private final ResponseMapper responseMapper;
    private final InventoryPublisher inventoryPublisher;
    private final FileService fileService;
    private final ProductAdminMapper productAdminMapper;

    public ProductService(ProductRepository productRepository, VariantRepository variantRepository,
            ProductMapper productMapper, ResponseMapper responseMapper, InventoryPublisher inventoryPublisher,
            FileService fileService,
            ProductAdminMapper productAdminMapper) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.productMapper = productMapper;
        this.responseMapper = responseMapper;
        this.inventoryPublisher = inventoryPublisher;
        this.fileService = fileService;
        this.productAdminMapper = productAdminMapper;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(responseMapper::productToResponse)
                .toList();
    }

    public List<ProductResponse> getAllProductsHot() {
        return productRepository.findAllByOrderBySoldDesc().stream()
                .map(responseMapper::productToResponse)
                .toList();
    }

    public List<ProductResponse> getSaleProducts() {
        return productRepository.findBySalePriceLessThanOriginalPriceWithVariants().stream()
                .map(responseMapper::productToResponse)
                .toList();
    }

    // public Page<ProductResponse> getAllPageProducts(Pageable pageable) {
    // return productRepository.findAll(pageable)
    // .map(responseMapper::productToResponse);
    // }

    public SimplifiedPageResponse<ProductColorResponse> getAllPageProducts(Pageable pageable) {
        Page<Variant> variantPage = variantRepository.findAll(pageable);
        List<ProductColorResponse> productColorResponses = variantPage.getContent().stream()
                .map(variant -> responseMapper.createProductResponse(variant.getProduct(), variant))
                .collect(Collectors.toList());

        Page<ProductColorResponse> page = new PageImpl<>(
                productColorResponses,
                pageable,
                variantPage.getTotalElements());
        return new SimplifiedPageResponse<>(page);
    }

    public List<AdminProductResponse> getAllProductsOrder(String query) {
        List<Product> products;
        if (query == null || query.trim().isEmpty()) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByNameContainingIgnoreCase(query);
        }
        return products.stream()
                .map(productAdminMapper::productToAdminProductResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return productRepository.findAll().stream()
                    .map(responseMapper::productToResponse)
                    .collect(Collectors.toList());
        }

        return productRepository.searchProducts(query).stream()
                .map(responseMapper::productToResponse)
                .collect(Collectors.toList());

    }

    public ProductDto getProductAdmin(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.PRODUCT_NOT_FOUND));
        return productMapper.productToDto(product);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.PRODUCT_NOT_FOUND));
        return responseMapper.productToResponse(product);
    }

    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        if (products.isEmpty()) {
            throw new AppException(ProductConstant.PRODUCT_NOT_FOUND);
        }
        return products.stream()
                .map(responseMapper::productToResponse)
                .collect(Collectors.toList());
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
        Product updatedProduct = productRepository.save(existingProduct);

        // Cập nhật tồn kho
        if (updatedProduct.getVariants() == null || updatedProduct.getVariants().isEmpty()) {
            if (request.getStock() != null) {
                inventoryPublisher.publishInventoryEvent(
                        EventType.UPDATE_INVENTORY,
                        updatedProduct.getSku(),
                        null,
                        request.getStock());
            }
        } else {
            for (Variant variant : updatedProduct.getVariants()) {
                if (variant.getStockQuantity() != null) {
                    inventoryPublisher.publishInventoryEvent(
                            EventType.UPDATE_INVENTORY,
                            updatedProduct.getSku(),
                            variant.getSku(),
                            variant.getStockQuantity());
                }
            }
        }

        return productMapper.productToDto(updatedProduct);
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

    public VariantDto addVariant(VariantCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ProductConstant.PRODUCT_NOT_FOUND));
        Variant variant = productMapper.variantToCreateEntity(request, product);
        Variant savedVariant = variantRepository.save(variant);
        inventoryPublisher.publishInventoryEvent(EventType.CREATE_INVENTORY,
                product.getSku(),
                variant.getSku(),
                variant.getStockQuantity());
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

    @Transactional
    public void updateProductInventory(InventoryEvent inventoryEvent) {
        System.out.println(">>1");
        System.out.println("Processing InventoryEvent: skuProduct=" + inventoryEvent.getSkuProduct() +
                ", skuVariant=" + inventoryEvent.getSkuVariant() +
                ", quantity=" + inventoryEvent.getQuantity());
        if (inventoryEvent.getSkuVariant() == null) {
            long skuProduct = 0;
            try {
                skuProduct = Long.parseLong(inventoryEvent.getSkuProduct());
            } catch (NumberFormatException e) {
                throw new AppException("Invalid SKU format");
            }
            System.out.println(">>11");

            Product product = productRepository.findById(skuProduct)
                    .orElseThrow(() -> new AppException("Sản phẩm không tồn tại"));

            int newStock = product.getStock() - inventoryEvent.getQuantity();
            System.out.println(">>12");

            product.setStock(newStock);
            int newSold = product.getSold() + inventoryEvent.getQuantity();
            System.out.println(">>13" + newSold);

            product.setSold(newSold);

            productRepository.save(product);
        } else {

            Variant variant = variantRepository.findByIdAndSize(
                    Long.parseLong(inventoryEvent.getSkuProduct()),
                    inventoryEvent.getSkuVariant())
                    .orElseThrow(() -> new AppException(
                            "Biến thể sản phẩm không tồn tại với id=" + inventoryEvent.getSkuProduct() +
                                    " và size=" + inventoryEvent.getSkuVariant()));
            System.out.println(">>14" + variant);

            int newVariantStock = variant.getStockQuantity() - inventoryEvent.getQuantity();
            System.out.println(">>15" + newVariantStock);

            variant.setStockQuantity(newVariantStock);
            Product product = variant.getProduct();
            int newSold = product.getSold() + inventoryEvent.getQuantity();
            product.setSold(newSold);
            System.out.println(">>16" + newVariantStock);

            productRepository.save(product);
            variantRepository.save(variant);
        }
        System.out.println(">>11");

    }

    @Transactional
    public void updatedRating(ProductEvent event) {
        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new AppException("Sản phẩm không tồn tại"));

        // Tính tổng điểm hiện tại
        float totalRating = product.getRating() * product.getRatingCount();

        // Tăng số lượng đánh giá
        int newRatingCount = product.getRatingCount() + 1;

        // Cộng thêm đánh giá mới
        float newTotalRating = totalRating + event.getFlagProduct();

        // Tính lại trung bình
        float newAverageRating = newTotalRating / newRatingCount;

        product.setRating(newAverageRating);
        product.setRatingCount(newRatingCount);

        productRepository.save(product);
    }

}
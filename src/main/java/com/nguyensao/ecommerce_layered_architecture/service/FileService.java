package com.nguyensao.ecommerce_layered_architecture.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.constant.GithubConstant;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.utils.FileValidation;

@Service
public class FileService {

    public String uploadImage(MultipartFile file) throws IOException {
        if (file != null) {
            GitHub github = GitHub.connectUsingOAuth(GithubConstant.GITHUB_TOKEN);
            GHRepository repository = github.getRepository(GithubConstant.REPO_NAME);
            String timestamp = String.valueOf(System.currentTimeMillis());
            String imagePath = "springboot/" + timestamp + "_" + file.getOriginalFilename();

            if (!FileValidation.isValidImage(file)) {
                throw new AppException("Chỉ chấp nhận file JPG, PNG, JPEG, GIF!");
            }

            repository.createContent()
                    .content(file.getBytes())
                    .path(imagePath)
                    .message("Tải thành công: " + file.getOriginalFilename())
                    .branch(GithubConstant.BRANCH)
                    .commit();

            return "https://raw.githubusercontent.com/" + GithubConstant.REPO_NAME + "/" + GithubConstant.BRANCH + "/"
                    + imagePath;
        }

        throw new AppException("File không được để trống!");
    }

    public List<String> uploadMultipleImages(MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            throw new AppException("Danh sách file không được để trống!");
        }

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String url = uploadImage(file);
                imageUrls.add(url);
            }
        }

        if (imageUrls.isEmpty()) {
            throw new AppException("Không có file hợp lệ được tải lên!");
        }
        return imageUrls;
    }

}

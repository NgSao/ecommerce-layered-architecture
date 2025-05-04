package com.nguyensao.ecommerce_layered_architecture.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GithubConstant {

    @Value("${GITHUB_TOKEN}")
    public static String GITHUB_TOKEN;

    @Value("${REPO_NAME}")
    public static String REPO_NAME;

    @Value("${BRANCH}")
    public static String BRANCH;
}

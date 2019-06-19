package club.mydlq.swagger.kubernetes.discovery;

import club.mydlq.swagger.kubernetes.param.KubernetesAutoConfig;
import club.mydlq.swagger.kubernetes.utils.FileUtils;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.io.*;

/**
 * Author: mydlq / 小豆丁
 * Blog:   http://www.mydlq.club
 * Github: https://github.com/my-dlq/
 *
 * Describe: Connection kubernetes
 */
@Slf4j
public class KubernetesConnect {

    private KubernetesAutoConfig kubernetesAutoConfig;

    public KubernetesConnect(KubernetesAutoConfig kubernetesAutoConfig) {
        this.kubernetesAutoConfig = kubernetesAutoConfig;
    }

    /**
     * 连接 Kubernetes 集群
     * Connection kubernetes
     */
    public void connection() {
        String token = kubernetesAutoConfig.getToken();
        String tokenPath = kubernetesAutoConfig.getTokenPath();
        String url = kubernetesAutoConfig.getUrl();
        String caPath = kubernetesAutoConfig.getCaPath();
        String formConfigPath = kubernetesAutoConfig.getFromConfigPath();
        boolean isFromCluster = kubernetesAutoConfig.isFromCluster();
        boolean isFromDefault = kubernetesAutoConfig.isFromDefault();
        boolean isValidateSSL = kubernetesAutoConfig.isValidateSsl();
        // form token
        if (StringUtils.isNotEmpty(tokenPath) && StringUtils.isNotEmpty(url)) {
            log.info("from token file connection kubernetes");
            token = FileUtils.readFile(tokenPath);
            connectFromToken(url, token, isValidateSSL, caPath);
        } else if (StringUtils.isNotEmpty(token) && StringUtils.isNotEmpty(url)) {
            log.info("from token connection kubernetes");
            connectFromToken(url, token, isValidateSSL, caPath);
        }
        // form cluster
        else if (isFromCluster) {
            log.info("from cluster env connection kubernetes");
            connectFromCluster();
        }
        // form config
        else if (StringUtils.isNotEmpty(formConfigPath)) {
            log.info("from config file connection kubernetes");
            connectFromConfig(formConfigPath);
        } else if (isFromDefault) {
            log.info("from $HOME/.kube/config connection kubernetes");
            connectFromSystemConfig();
        }
    }

    /**
     * 默认方式,从系统配置 $HOME/.kube/config 读取配置文件连接 Kubernetes 集群
     * By default, connect to the Kubernetes cluster by reading a configuration file from the system configuration $HOME/.kube/config
     */
    private void connectFromSystemConfig() {
        ApiClient apiClient = null;
        try {
            apiClient = Config.defaultClient();
        } catch (IOException e) {
            log.error("read config file error",e);
        }
        Configuration.setDefaultApiClient(apiClient);
    }

    /**
     * 从指定文件读取配置文件连接 Kubernetes 集群
     * Reads a configuration file from the specified file to connect to the Kubernetes cluster
     *
     * @param configPath
     */
    private void connectFromConfig(String configPath) {
        ApiClient apiClient = null;
        try {
            apiClient = Config.fromConfig(configPath);
        } catch (IOException e) {
            log.error("read config file error",e);
        }
        Configuration.setDefaultApiClient(apiClient);
    }

    /**
     * 通过 Token 连接 Kubernetes 集群
     * Connect to the Kubernetes cluster over Token
     *
     * @param url
     * @param token
     * @param validateSSL
     * @param caPath
     */
    private void connectFromToken(String url, String token, boolean validateSSL, String caPath) {
        ApiClient apiClient = Config.fromToken(url, token, validateSSL);
        // validateSSL
        if (validateSSL) {
            try {
                apiClient.setSslCaCert(new FileInputStream(caPath));
            } catch (FileNotFoundException e) {
                log.error("Check that the certificate file exists");
            }
        }
        Configuration.setDefaultApiClient(apiClient);
    }

    /**
     * 如果在 kubernetes 集群内，则利用 kubernetes 环境
     * If within the kubernetes cluster, the kubernetes environment is utilized
     */
    private void connectFromCluster() {
        ApiClient apiClient = null;
        try {
            apiClient = Config.fromCluster();
        } catch (IOException e) {
            log.error("read container token error", e);
        }
        Configuration.setDefaultApiClient(apiClient);
    }

}

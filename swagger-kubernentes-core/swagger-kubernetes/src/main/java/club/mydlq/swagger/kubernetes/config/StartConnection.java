package club.mydlq.swagger.kubernetes.config;

import club.mydlq.swagger.kubernetes.discovery.KubernetesDiscovery;
import club.mydlq.swagger.kubernetes.discovery.KubernetesConnect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

/**
 * SpringBoot 应用启动成功后首先要执行的任务
 */
/**
 * Author: mydlq / 小豆丁
 * Blog:   http://www.mydlq.club
 * Github: https://github.com/my-dlq/
 *
 * Describe: The first task to execute when SpringBoot boots
 */
@Order(value = 0)
public class StartConnection implements CommandLineRunner {

    @Autowired
    KubernetesConnect connectKubernetes;
    @Autowired
    KubernetesDiscovery kubernetesDiscovery;

    @Override
    public void run(String... args) throws Exception {
        // Connection kubernetes
        connectKubernetes.connection();
        // Init service data
        kubernetesDiscovery.freshServiceList();
    }

}

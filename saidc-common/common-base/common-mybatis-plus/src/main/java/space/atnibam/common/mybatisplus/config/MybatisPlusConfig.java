package space.atnibam.common.mybatisplus.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: MybatisPlusConfig
 * @Description: MybatisPlus的配置类
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-02 21:52
 **/
@Configuration
public class MybatisPlusConfig {

    /**
     * 创建 MybatisPlusInterceptor Bean。
     *
     * @return MybatisPlusInterceptor 对象
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建一个 MybatisPlusInterceptor 实例
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 创建一个分页拦截器 PaginationInnerInterceptor 实例
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();

        // 设置该分页插件在处理关联查询时，是否优化为单次查询
        paginationInnerInterceptor.setOptimizeJoin(true);

        // 设置数据库类型为 MYSQL，这是为了让分页插件知道如何生成对应的分页 SQL 语句
        paginationInnerInterceptor.setDbType(DbType.MYSQL);

        // 设置当请求的页面大于最大页后操作，true 调整到首页，false 继续请求 默认 false
        paginationInnerInterceptor.setOverflow(true);

        // 将分页拦截器添加到 MybatisPlusInterceptor 中
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 创建乐观锁拦截器实例
        OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor = new OptimisticLockerInnerInterceptor();

        // 将乐观锁拦截器添加到 MybatisPlusInterceptor 中
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor);

        // 返回创建好的 MybatisPlusInterceptor 实例
        return interceptor;
    }


}

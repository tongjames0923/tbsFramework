package tbs.framework.sql.utils;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;

@Component
public class BatchUtil {
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    ILogger logger;

    private static BatchUtil batchUtil = null;

    public BatchUtil(final LogUtil util) {
        this.logger = util.getLogger(BatchUtil.class.getName());
        if (null == BatchUtil.batchUtil) {
            BatchUtil.batchUtil = this;
        }
    }

    public static BatchUtil getInstance() {
        return BatchUtil.batchUtil;
    }

    /**
     * 批量插入
     *
     * @param data        数据
     * @param batchSize   单次数据提交量
     * @param ignoreError
     * @param mapper
     * @param <T>
     */
    public static <T> void batchInsert(List<T> data, long batchSize, boolean ignoreError,
        Class<? extends BaseMapper<T>> mapper) {
        BatchUtil.batchUtil.batch(data, batchSize, (d, m) -> {
            m.insert(d);
        }, mapper, ignoreError);
    }

    /**
     * 批量插入          batchUpdate(data,batchSize, false, mapper);
     *
     * @param data      数据
     * @param batchSize 单次数据量
     * @param mapper    mapper
     * @param <T>
     */
    public static <T> void batchInsert(List<T> data, long batchSize, Class<? extends BaseMapper<T>> mapper) {
        BatchUtil.batchUtil.batch(data, batchSize, (d, m) -> {
            m.insert(d);
        }, mapper, false);
    }

    /**
     * 批量插入          batchInsert(data, 200, false, mapper);
     *
     * @param data   数据
     * @param mapper mapper
     * @param <T>
     */
    public static <T> void batchInsert(List<T> data, Class<? extends BaseMapper<T>> mapper) {
        BatchUtil.batchUtil.batch(data, 300, (d, m) -> {
            m.insert(d);
        }, mapper, false);
    }

    /**
     * 批量更新 根据主键
     *
     * @param data        数据
     * @param batchSize   单次数据提交量
     * @param ignoreError
     * @param mapper
     * @param <T>
     */
    public static <T> void batchUpdate(List<T> data, long batchSize, boolean ignoreError,
        Class<? extends BaseMapper<T>> mapper) {
        BatchUtil.batchUtil.batch(data, batchSize, (d, m) -> {
            m.updateByPrimaryKey(d);
        }, mapper, ignoreError);
    }

    /**
     * 批量更新          batchUpdate(data,batchSize, false, mapper);
     *
     * @param data      数据
     * @param batchSize 单次数据量
     * @param mapper    mapper
     * @param <T>
     */
    public static <T> void batchUpdate(List<T> data, long batchSize, Class<? extends BaseMapper<T>> mapper) {
        BatchUtil.batchUpdate(data, batchSize, false, mapper);
    }

    /**
     * 批量更新          batchUpdate(data, 200, false, mapper);
     *
     * @param data   数据
     * @param mapper mapper
     * @param <T>
     */
    public static <T> void batchUpdate(List<T> data, Class<? extends BaseMapper<T>> mapper) {
        BatchUtil.batchUpdate(data, 200, mapper);
    }

    /**
     * 批量操作根方法
     *
     * @param data        数据列表
     * @param batchSize   一次提交的数量
     * @param work        mapper操作，如insert/update等
     * @param mapper      所需的mapper类型
     * @param ignoreError 忽略操作中的错误
     * @param <T>
     */
    public <T> void batch(List<T> data, long batchSize, BiConsumer<T, BaseMapper<T>> work,
        Class<? extends BaseMapper<T>> mapper, boolean ignoreError) {
        if (0L >= batchSize) {
            batchSize = 1000L;
        }
        if (null == work) {
            throw new NullPointerException("必须有处理的操作");
        }
        final SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        final BaseMapper<T> baseMapper = sqlSession.getMapper(mapper);
        if (null == baseMapper) {
            sqlSession.close();
            throw new NoSuchElementException("不存在的Mapper");
        }

        long cnt = 0;
        for (final T d : data) {
            try {
                work.accept(d, baseMapper);
            } catch (Exception e) {
                this.logger.error(e, e.getMessage());
                if (!ignoreError) {
                    sqlSession.rollback();
                    break;
                }
            }
            cnt = (cnt + 1) % batchSize;
            if (0 == cnt) {
                sqlSession.commit();
                sqlSession.clearCache();
            }
        }
        sqlSession.commit();
        sqlSession.close();
    }
}

package tbs.framework.sql.interfaces.mappers;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.update.batch.BatchUpdateMapper;
import tk.mybatis.mapper.additional.update.batch.BatchUpdateSelectiveMapper;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * @author abstergo
 */
public interface IFrameworkMapper<T, K>
    extends BaseMapper<T>, InsertListMapper<T>, BatchUpdateMapper<T>, BatchUpdateSelectiveMapper<T>,
    IdListMapper<T, K>,QueryMapper<T> {
}

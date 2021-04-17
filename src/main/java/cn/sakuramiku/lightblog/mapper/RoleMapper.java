package cn.sakuramiku.lightblog.mapper;


import cn.sakuramiku.lightblog.entity.Role;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 角色SQL映射
 *
 * @author LYY
 */
public interface RoleMapper {

    /**
     * 删除角色
     *
     * @param id  角色ID
     * @return 是否成功
     */
    Boolean delete(@Param("id") Long id);

    /**
     * 添加角色
     *
     * @param record 角色
     * @return 是否成功
     */
    Boolean add(@NonNull Role record);

    /**
     * 添加角色
     * <p>
     * 插入一条记录到中间表
     *
     * @param userId
     * @param roleId
     * @return
     */
    Boolean insert(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 获取角色
     *
     * @param id 角色ID
     * @return 角色
     */
    Role get(@NonNull @Param("id") Long id);

    @Select("select * from `role` where name = #{name}")
    Role getByName(@Param("name") String name);

    /**
     * 搜索角色
     *
     * @param keyword 关键字
     * @return 角色列表
     */
    List<Role> find(@Nullable @Param("keyword") String keyword);

    /**
     * 搜索用户角色
     *
     * @param userId  用户ID
     * @param keyword 关键字
     * @return 角色列表
     */
    List<Role> search(@Nullable @Param("userId") Long userId, @Nullable @Param("keyword") String keyword);

    Long getUserCount(Long roleId);

    /**
     * 修改角色
     *
     * @param record 参数
     * @return 是否成功
     */
    Boolean update(@NonNull Role record);

    Boolean batchInsert(List<BatchInsertParam> params);

    Boolean deleteForUser(Long userId);
}
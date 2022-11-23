package com.atguigu.yygh.cmn.service;


import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-11-03
 */
public interface DictService extends IService<Dict> {

    //根据id查询子数据字典列表
    List<Dict> findChildData(Long id);

    //下载数据字典
    void download(HttpServletResponse response);

    /**
     * 上传数据字典
     * @param file 选中文件路径
     */
    void upload(MultipartFile file) throws IOException;

    /**
     * 根据value查询省市区名字
     * @param value
     * @return
     */
    String getNameByValue(Long value);

    /**
     * 查医院类型名称
     * @param value
     * @param dictCode
     * @return
     */
    String getNameByValueAndCode(Long value, String dictCode);

    /**
     * 根据dictcode查询子级列表
     * @param dictCode
     * @return
     */
    List<Dict> getChildListByParentDictCode(String dictCode);

    /**
     * 根据省的id当做市的parent_id查询市列表
     * @param parentId
     * @return
     */
    List<Dict> getCityListByParentId(Long parentId);
}

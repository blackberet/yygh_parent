package com.atguigu.yygh.cmn.service.impl;


import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2022-11-03
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    //根据数据id查询子数据列表
    @Cacheable(value = "dict", key = "'selectDictList'+#id")
    @Override
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        //往每个Dict中的hasChildren赋值
        for (Dict dict : dictList) {
            //根据每个List中的Dict的每个id调用isChildren()方法查询是否有下级,并且赋值
            dict.setHasChildren(isChildren(dict.getId()));
        }
        return dictList;
    }

    //下载数据字典
    @SneakyThrows
    @Override
    public void download(HttpServletResponse response) {
        List<Dict> dictList = baseMapper.selectList(null);
        //String fileName = "D:\\Desktop\\student.xlsx";

        ArrayList<DictEeVo> dictEeVos = new ArrayList<>();
        for (Dict dict : dictList) {
            DictEeVo dictEeVo = new DictEeVo();
            //方法一:
            //一个一个属性赋值
            //dictEeVo.setId(dict.getId());

            //方法二:spring的工具类copy对象
            BeanUtils.copyProperties(dict,dictEeVo);//要求:原对象和复制对象属性名要一致,类型也要一致
            dictEeVos.add(dictEeVo);
        }
        EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet(0).doWrite(dictEeVos);

    }


    /**
     * 上传数据字典
     * @param file 选中文件路径
     */
    @CacheEvict(value = "dict",allEntries = true)
    @Override
    public void upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(),DictEeVo.class, new DictListener(baseMapper)).sheet().doRead();
    }

    //远程调用根据value获取省市区名字
    @Override
    public String getNameByValue(Long value) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("value",value);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        if (dict != null) {
            return dict.getName();
        }
        return null;
    }

    //根据dictCode和value查询医院等级
    @Override
    public String getNameByValueAndCode(Long value, String dictCode) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        Long id = dict.getId();

        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        queryWrapper.eq("value",value);
        Dict one = baseMapper.selectOne(queryWrapper);
        return one.getName();
    }

    @Override
    public List<Dict> getChildListByParentDictCode(String dictCode) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        Long id = dict.getId();

        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        return dictList;
    }

    @Override
    public List<Dict> getCityListByParentId(Long parentId) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentId);
        List<Dict> dictList = baseMapper.selectList(queryWrapper);
        return dictList;
    }


    //判断id下面是否有子节点
    public boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0){
            //有子节点
            return true;
        }else {
            return false;
        }
    }

}

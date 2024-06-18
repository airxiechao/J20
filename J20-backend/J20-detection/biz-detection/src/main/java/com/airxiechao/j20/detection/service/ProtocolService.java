package com.airxiechao.j20.detection.service;

import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.detection.api.pojo.protocol.Protocol;
import com.airxiechao.j20.detection.api.service.IProtocolService;
import com.airxiechao.j20.detection.db.record.ProtocolRecord;
import com.airxiechao.j20.detection.db.reposiroty.IProtocolRepository;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProtocolService implements IProtocolService {

    private IProtocolRepository protocolRepository = ApplicationContextUtil.getContext().getBean(IProtocolRepository.class);

    @Override
    public Protocol get(String code) throws NotFoundException {
        Optional<ProtocolRecord> opt = protocolRepository.findById(code);
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }

        return buildProtocol(opt.get());
    }

    @Override
    public boolean exists(String code) {
        return protocolRepository.existsByCodeIgnoreCase(code);
    }

    @Override
    public PageVo<Protocol> list(String code, Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc) {
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "code";
        }

        Sort sort;
        if (null != orderAsc && !orderAsc) {
            sort = Sort.by(orderBy).descending();
        } else {
            sort = Sort.by(orderBy).ascending();
        }

        if (null == pageNo) {
            pageNo = 1;
        }
        if (null == pageSize) {
            pageSize = 20;
        }

        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize, sort);

        Page<ProtocolRecord> page;
        if (StringUtils.isNotBlank(code)) {
            page = protocolRepository.findByCodeContainingIgnoreCase(code, pageRequest);
        } else {
            page = protocolRepository.findAll(pageRequest);
        }

        List<Protocol> list = page.stream()
                .map(r -> buildProtocol(r))
                .collect(Collectors.toList());

        return new PageVo<>(page.getTotalElements(), list);
    }

    @Override
    public List<Protocol> list(String code) {
        String orderBy =  "code";
        Sort sort = Sort.by(orderBy).ascending();

        List<ProtocolRecord> records;
        if (StringUtils.isNotBlank(code)) {
            records = protocolRepository.findByCodeContainingIgnoreCase(code, sort);
        } else {
            records = protocolRepository.findAll(sort);
        }

        List<Protocol> list = records.stream()
                .map(r -> buildProtocol(r))
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public Protocol add(Protocol protocol) {
        ProtocolRecord record = buildProtocolRecord(protocol);
        return buildProtocol(protocolRepository.save(record));
    }

    @Override
    public void update(Protocol protocol) {
        ProtocolRecord record = buildProtocolRecord(protocol);
        protocolRepository.save(record);
    }

    @Override
    public void delete(String code) {
        protocolRepository.deleteById(code);
    }

    private Protocol buildProtocol(ProtocolRecord record) {
        return new Protocol(
                record.getCode(),
                JSON.parseObject(record.getFieldSchema(), new TypeReference<>() {
                })
        );
    }

    private ProtocolRecord buildProtocolRecord(Protocol protocol) {
        ProtocolRecord record = new ProtocolRecord();
        record.setCode(protocol.getCode());
        record.setFieldSchema(JSON.toJSONString(protocol.getFieldSchema()));

        return record;
    }
}

package com.codestates.dto;

import com.codestates.member.dto.MemberDto;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;


@Getter
public class MultiResponseDto<T> {
    private List<T> data;
    private PageInfo pageInfo;

    public MultiResponseDto(List<MemberDto.Response> data, Page page) {
        this.data = (List<T>) data;
        this.pageInfo = new PageInfo(page.getNumber() + 1,
                page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}

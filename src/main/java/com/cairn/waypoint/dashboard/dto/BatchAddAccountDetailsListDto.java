package com.cairn.waypoint.dashboard.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchAddAccountDetailsListDto {

  private List<BatchAddAccountDetailsDto> accountBatch;
}

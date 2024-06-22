package com.cairn.waypoint.dashboard.dto.authorization;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchAddAccountDetailsListDto {

  private List<BatchAddAccountDetailsDto> accountBatch;
}

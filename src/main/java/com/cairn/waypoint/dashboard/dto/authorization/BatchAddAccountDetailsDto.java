package com.cairn.waypoint.dashboard.dto.authorization;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAddAccountDetailsDto {

  private String username;
  private String firstName;
  private String lastName;
  private Set<String> roleNames;
  private String email;
  private String phoneNumber;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private String zip;
  private String coClientUsername;
  private String parentAccountUsername;

  private String password;
}

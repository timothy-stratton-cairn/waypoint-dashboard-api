package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.Protocol;
import com.cairn.waypoint.dashboard.entity.enumeration.RecurrenceTypeEnum;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProtocolRepository extends JpaRepository<Protocol, Long> {

  List<Protocol> findByProtocolTemplate_Id(Long protocolTemplateId);

  List<Protocol> findByAssignedHouseholdId(Long householdId);

  List<Protocol> findByRecurrenceType(RecurrenceTypeEnum recurrenceTypeEnum);

  @Query(value =
      "SELECT * "
          + "FROM protocol p "
          + "WHERE "
          + "    p.created + "
          + "    INTERVAL p.reoccur_in_years YEAR +"
          + "    INTERVAL p.reoccur_in_months MONTH +"
          + "    INTERVAL p.reoccur_in_days DAY >= NOW()"
          + "  AND "
          + "    p.created +"
          + "    INTERVAL p.reoccur_in_years YEAR +"
          + "    INTERVAL p.reoccur_in_months MONTH +"
          + "    INTERVAL p.reoccur_in_days DAY <= :limitingDate"
          + "  AND "
          + "    (p.protocol_status_id = :inProgressProtocolStatusId "
          + "      OR p.protocol_status_id = :completedProtocolStatusId)"
          + "  AND "
          + "    p.recurrence_type_id = :scheduledRecurrenceTypeId",
      nativeQuery = true)
  List<Protocol> findAllUpcomingProtocolsToDate(@Param("limitingDate") LocalDate limitingDate,
      @Param("inProgressProtocolStatusId") Long inProgressProtocolStatusId,
      @Param("completedProtocolStatusId") Long completedProtocolStatusId,
      @Param("scheduledRecurrenceTypeId") Long scheduledRecurrenceTypeId);
}

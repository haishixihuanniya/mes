package com.qcadoo.mes.cmmsMachineParts.reports.xls.timeUsage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.jdbc.core.RowMapper;

import com.qcadoo.mes.cmmsMachineParts.reports.xls.timeUsage.dto.TimeUsageDTO;


public class TimeUsageRowMapper implements RowMapper<TimeUsageDTO> {

    @Override
    public TimeUsageDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeUsageDTO timeUsage = new TimeUsageDTO();
        timeUsage.setWorker(rs.getString("worker"));
        timeUsage.setStartDate(rs.getDate("startDate"));
        timeUsage.setNumber(rs.getString("number"));
        timeUsage.setEventType(rs.getString("event_type"));
        timeUsage.setType(rs.getString("type"));
        timeUsage.setState(rs.getString("state"));
        timeUsage.setObject(rs.getString("object"));
        timeUsage.setParts(rs.getString("parts"));
        timeUsage.setDescription(rs.getString("description"));
        timeUsage.setDuration(rs.getInt("duration"));
        DateTime startDateTime = new DateTime(rs.getTimestamp("registeredStart"));
        Date endDate = rs.getTimestamp("registeredEnd");
        if (endDate != null) {
            DateTime endDateTime = new DateTime(endDate);
            timeUsage.setRegisteredTime(Minutes.minutesBetween(startDateTime, endDateTime).getMinutes());
        } else {
            timeUsage.setRegisteredTime(0);
        }
        return timeUsage;
    }

}

package com.westudy.alarm.mapper;

import com.westudy.alarm.entity.Alarm;
import com.westudy.alarm.dto.AlarmResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AlarmMapper {
    void insertAlarm(Alarm alarm);
    List<AlarmResponseDTO> findByReceiverId(Long receiverId);
    int countUnreadByReceiverId(Long receiverId);
    void markAsRead(Long alarmId);
    void markAllAsRead(Long receiverId);
}

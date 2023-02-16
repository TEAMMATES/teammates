package teammates.storage.sqlconverter;

import java.util.List;

import com.google.common.reflect.TypeToken;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import teammates.common.util.JsonUtils;
import teammates.common.datatransfer.FeedbackParticipantType;

@Converter
public class FeedbackParticipantTypeListConverter implements AttributeConverter<List<FeedbackParticipantType>, String>  {
    
    @Override
    public String convertToDatabaseColumn(List<FeedbackParticipantType> attribute) {
        return JsonUtils.toJson(attribute);
    }

    @Override
    public List<FeedbackParticipantType> convertToEntityAttribute(String dbData) {
        return JsonUtils.fromJson(dbData, new TypeToken<List<FeedbackParticipantType>>(){}.getType());
    }
}

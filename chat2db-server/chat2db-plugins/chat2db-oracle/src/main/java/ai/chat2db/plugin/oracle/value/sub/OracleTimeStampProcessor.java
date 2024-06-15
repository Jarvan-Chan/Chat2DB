package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: zgq
 * @date: 2024年06月05日 16:20
 */
public class OracleTimeStampProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return wrap(dataValue.getValue(), dataValue.getScale());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        // TODO: datagrip对timestampLTZ的处理是不受时区影响的，但其实这个字段就是为了可以协同时区问题的，有待商讨
        Timestamp timestamp = dataValue.getTimestamp();
        int scale = dataValue.getScale();
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        StringBuilder templateBuilder = new StringBuilder("yyyy-MM-dd HH:mm:ss");
        if (scale != 0) {
            templateBuilder.append(".");
            templateBuilder.append("S".repeat(scale));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(templateBuilder.toString());
        return localDateTime.format(formatter);
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return wrap(convertJDBCValueByType(dataValue), dataValue.getScale());
    }

    private String wrap(String value, int scale) {
        if (scale == 0) {
            return String.format(OracleDmlValueTemplate.DATE_TEMPLATE, value);
        }
        return String.format(OracleDmlValueTemplate.TIMESTAMP_TEMPLATE, value, scale);
    }
}

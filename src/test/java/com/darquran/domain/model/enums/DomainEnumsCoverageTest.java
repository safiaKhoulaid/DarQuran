package com.darquran.domain.model.enums;

import com.darquran.domain.model.enums.courses.CourseLevel;
import com.darquran.domain.model.enums.courses.CourseStatus;
import com.darquran.domain.model.enums.lessons.LessonType;
import com.darquran.domain.model.enums.live.LiveAccessType;
import com.darquran.domain.model.enums.live.LiveSessionStatus;
import com.darquran.domain.model.enums.notification.UserNotificationType;
import com.darquran.domain.model.enums.resources.ResourceType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Couverture des enums du domaine (clinit + valueOf).
 */
class DomainEnumsCoverageTest {

    @Test
    void allDomainEnums_roundTripValueOf() {
        assertEnum(Role.class);
        assertEnum(Section.class);
        assertEnum(AbsenceStatus.class);
        assertEnum(CourseLevel.class);
        assertEnum(CourseStatus.class);
        assertEnum(LessonType.class);
        assertEnum(LiveSessionStatus.class);
        assertEnum(LiveAccessType.class);
        assertEnum(UserNotificationType.class);
        assertEnum(ResourceType.class);
    }

    private <E extends Enum<E>> void assertEnum(Class<E> type) {
        E[] constants = type.getEnumConstants();
        assertThat(constants).isNotEmpty();
        for (E c : constants) {
            assertThat(Enum.valueOf(type, c.name())).isSameAs(c);
        }
    }
}


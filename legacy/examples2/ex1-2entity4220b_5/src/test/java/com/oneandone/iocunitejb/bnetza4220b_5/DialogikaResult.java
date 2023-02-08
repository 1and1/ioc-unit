package com.oneandone.iocunitejb.bnetza4220b_5;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;

/**
 * @author aschoerk
 */
public class DialogikaResult {
    private String id;
    private Integer refId;
    private Integer validStartDate;
    private Integer validEndDate;
    private Integer modificationDate;

    public static final class DialogikaResultBuilder {
        private String id;
        private Integer refId;
        private Integer validStartDate;
        private Integer validEndDate;
        private Integer modificationDate;

        public DialogikaResultBuilder() {}

        public static DialogikaResultBuilder aDialogikaResult() { return new DialogikaResultBuilder(); }

        public DialogikaResultBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DialogikaResultBuilder withRefId(Integer refId) {
            this.refId = refId;
            return this;
        }

        public DialogikaResultBuilder withValidStartDate(Integer validStartDate) {
            this.validStartDate = validStartDate;
            return this;
        }

        public DialogikaResultBuilder withValidEndDate(Integer validEndDate) {
            this.validEndDate = validEndDate;
            return this;
        }

        public DialogikaResultBuilder withModificationDate(Integer modificationDate) {
            this.modificationDate = modificationDate;
            return this;
        }

        public DialogikaResultBuilder but() { return aDialogikaResult().withId(id).withRefId(refId).withValidStartDate(validStartDate).withValidEndDate(validEndDate).withModificationDate(modificationDate); }

        public DialogikaResult build() {
            DialogikaResult dialogikaResult = new DialogikaResult();
            dialogikaResult.validEndDate = this.validEndDate;
            dialogikaResult.validStartDate = this.validStartDate;
            dialogikaResult.id = this.id;
            dialogikaResult.modificationDate = this.modificationDate;
            dialogikaResult.refId = this.refId;
            return dialogikaResult;
        }
    }

    public void equalsArray(Object[] values) {
        Assertions.assertArrayEquals(new Object[] {id, refId, validStartDate, validEndDate, modificationDate}, values);
    }

    private boolean isEqual(Object[] values) {
        return Arrays.equals(new Object[] {id, refId, validStartDate, validEndDate, modificationDate},values);
    }

    public boolean foundInList(List<Object[]> list) {
        for (Object[] o: list) {
            if (isEqual(o))
                return true;
        }
        return false;
    }
}

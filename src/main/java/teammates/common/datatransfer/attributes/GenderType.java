package teammates.common.datatransfer.attributes;

public enum GenderType {
    male {
        public String toString() {
            return "male";
        }
    },
    female {
        public String toString() {
            return "female";
        }
    },
    other {
        public String toString() {
            return "other";
        }
     },
    invalid {
        public String toString() {
            return "invalid gender";
        }
     }
}

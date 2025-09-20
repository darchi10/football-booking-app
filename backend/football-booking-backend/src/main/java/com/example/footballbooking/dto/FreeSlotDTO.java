package com.example.footballbooking.dto;

import lombok.Data;

@Data
public class FreeSlotDTO {
    private String startTime;
    private String endTime;
    private String fieldName;
    private String message;
    private String date;

    public static class Builder {
        private String startTime;
        private String endTime;
        private String fieldName;
        private String message;
        private String date;

        public Builder withStartTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder withEndTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder withFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withDate(String date) {
            this.date = date;
            return this;
        }

        public FreeSlotDTO build() {
            FreeSlotDTO freeSlotDTO = new FreeSlotDTO();
            freeSlotDTO.setStartTime(this.startTime);
            freeSlotDTO.setEndTime(this.endTime);
            freeSlotDTO.setFieldName(this.fieldName);
            freeSlotDTO.setMessage(this.message);
            freeSlotDTO.setDate(this.date);
            return freeSlotDTO;
        }
    }
}

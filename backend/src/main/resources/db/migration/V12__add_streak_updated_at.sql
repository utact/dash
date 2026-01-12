-- Add streak_updated_at column to studies table for O(1) streak calculation
ALTER TABLE studies ADD COLUMN streak_updated_at DATE;

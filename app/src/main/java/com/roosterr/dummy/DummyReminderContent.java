package com.roosterr.dummy;

import com.roosterr.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample name for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyReminderContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyReminder> ITEMS = new ArrayList<DummyReminder>();
    public static final List<DummyReminder> ITEMS_SMALL = new ArrayList<DummyReminder>();

    public static Map<String, Integer> TYPES = new HashMap<>();
    public static Map<String, Integer> TRIGGERS = new HashMap<>();
    public static Map<String, Integer> TRIGGERS_SMALL = new HashMap<>();

    private static final int COUNT = 5;

    static {
        TYPES.put("To Do", R.drawable.ic_to_do);
        TYPES.put("Anniversary", R.drawable.ic_anniversary);
        TYPES.put("Birthday", R.drawable.ic_birthday);
        TYPES.put("Bill Pay", R.drawable.ic_bill_pay);
        TYPES.put("Meeting", Integer.valueOf(R.drawable.ic_meeting));

        TRIGGERS.put("Alarm", R.drawable.ic_alarms_24dp);
        TRIGGERS.put("Scheduled SMS", R.drawable.ic_message_24dp);
        TRIGGERS.put("Call Reminder", R.drawable.ic_call_24dp);

        TRIGGERS_SMALL.put("Alarm", R.drawable.ic_alarms_16dp);
        TRIGGERS_SMALL.put("SMS", R.drawable.ic_message_16dp);
        TRIGGERS_SMALL.put("Call", R.drawable.ic_call_16dp);

        // Add some sample items.
        ITEMS.add(new DummyReminder("1", "Reminder 1", "To Do", "Alarm", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS.add(new DummyReminder("2", "Reminder 2", "Anniversary", "Call Reminder", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS.add(new DummyReminder("3", "Reminder 3", "Birthday", "Scheduled SMS", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS.add(new DummyReminder("4", "Reminder 4", "Anniversary", "Alarm", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS.add(new DummyReminder("5", "Reminder 5", "To Do", "Call Reminder", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS.add(new DummyReminder("6", "Reminder 6", "Bill Pay", "Scheduled SMS", "01-NOV-2016", "12:99 PM", "Never repeat"));

        ITEMS_SMALL.add(new DummyReminder("1", "Reminder 1", "To Do", "Alarm", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS_SMALL.add(new DummyReminder("2", "Reminder 2", "Anniversary", "Call", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS_SMALL.add(new DummyReminder("3", "Reminder 3", "Birthday", "SMS", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS_SMALL.add(new DummyReminder("4", "Reminder 4", "Anniversary", "Alarm", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS_SMALL.add(new DummyReminder("5", "Reminder 5", "To Do", "Call", "01-NOV-2016", "12:99 PM", "Never repeat"));
        ITEMS_SMALL.add(new DummyReminder("6", "Reminder 6", "Bill Pay", "SMS", "01-NOV-2016", "12:99 PM", "Never repeat"));
    }

    /**
     * A dummy item representing a piece of name.
     */
    public static class DummyReminder {
        public final String id;
        public final String type;
        public final String trigger;
        public final String content;
        public final String date;
        public final String time;
        public final String repeat;

        public DummyReminder(String id, String content, String type, String trigger, String date, String time, String repeat) {
            this.id = id;
            this.content = content;
            this.type = type;
            this.trigger = trigger;
            this.date = date;
            this.time = time;
            this.repeat = repeat;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}

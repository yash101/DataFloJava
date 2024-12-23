package sh.devya.DataFloJava2.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Message {
  private int streamID = null;
  private int sourceID = null;
  private String topic = null;
  private List<Entry<String, String>> headers = new ArrayList<>();
  private String data = null;
}

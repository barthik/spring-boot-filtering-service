package filtering.service;

import filtering.service.filtering.util.ComposedObject;
import filtering.service.filtering.util.DeepObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilteringServiceTests {

    @Autowired
    private FilteringService service;

    @Test
    public void contextLoads() {
        Map<String, Object> objects = new HashMap<>();
        objects.put("abe5d08c-116a-4416-952c-b4cb529c4046", Arrays.asList(123L, 14587L));

        Map<String, String> setting = new HashMap<>();
        setting.put("4c2c221a-d66c-4a76-9706-69d311859d5f", "value1");
        setting.put("e493f2ed-46cd-4c9a-91b4-afa9d4048701", "value2");

        DeepObject deep = new DeepObject();
        deep.setId("c6cfa0e4-55c0-4244-bf42-9644d71c2b95");
        deep.setName("Deep String name{}))(/*-+.>,:");
        deep.setNumber(963.08);
        deep.setSetting(setting);
        deep.setIgnoredField("Ignored field");

        ComposedObject composed = new ComposedObject();
        composed.setId("c2d3b733-e611-4adb-8e18-aac5db02a5fc");
        composed.setName("Composed String name{}))(/*-+.>,:");
        composed.setNumber(null);
        composed.setAttributes(Arrays.asList("6539b44e-287d-4002-89b2-a9fdf874e93a","36817a50-bbaa-41d6-9a30-77342e58095e","6c2591da-41c6-400b-a92c-f5cf5097a356"));
        composed.setObjects(objects);
        composed.setDeepObject(deep);
        composed.setIgnoredField(9998);

        service.updateFilters(composed, composed.getId());
    }

}

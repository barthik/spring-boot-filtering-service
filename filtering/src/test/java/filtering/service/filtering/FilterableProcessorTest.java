package filtering.service.filtering;

import filtering.service.filtering.util.ComposedObject;
import filtering.service.filtering.util.DeepObject;
import filtering.service.filtering.util.SimpleComposedObject;
import filtering.service.filtering.util.SimpleObject;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FilterableProcessorTest {

    @Test
    public void detectionTest() {
        SimpleObject simple = new SimpleObject();
        simple.setId("5afd4494-6743-495e-b019-433fde0fa594");
        simple.setName("String name{}))(/*-+.>,:");
        simple.setNumber(123L);
        simple.setIgnoredField(987);

        Map<String, Object> result = FilterableProcessor.discover(simple);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertThat(result, Matchers.hasEntry("name", "String name{}))(/*-+.>,:"));
        Assert.assertThat(result, Matchers.hasEntry("number", 123L));
    }

    @Test
    public void nestedDetectionTest() {
        DeepObject deep = new DeepObject();
        deep.setId("f0b4917b-9d9d-4bdd-85a3-d5f833d3c646");
        deep.setName("Deep String name{}))(/*-+.>,:");
        deep.setNumber(963L);
        deep.setSetting(null);
        deep.setIgnoredField("Ignored field");

        SimpleComposedObject composed = new SimpleComposedObject();
        composed.setId("3882222f-bb73-4a30-87f0-20f36270219f");
        composed.setDeepObject(deep);
        composed.setIgnoredField(12877);

        Map<String, Object> result = FilterableProcessor.discover(composed);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertThat(result, Matchers.hasEntry("deepObject.id", "f0b4917b-9d9d-4bdd-85a3-d5f833d3c646"));
    }

    @Test
    public void nestedDetectionTest2() {
        Map<String, Object> objects = new HashMap<>();
        objects.put("1", Arrays.asList(123L, 14587L));

        Map<String, String> setting = new HashMap<>();
        setting.put("key1", "value1");
        setting.put("key2", "value2");

        DeepObject deep = new DeepObject();
        deep.setId("ad76d0b8-aead-45c8-862b-fd9c8cc0f446");
        deep.setName("Deep String name{}))(/*-+.>,:");
        deep.setNumber(963.08);
        deep.setSetting(setting);
        deep.setIgnoredField("Ignored field");

        ComposedObject composed = new ComposedObject();
        composed.setId("91ed8b26-021b-4701-aaef-3ca43d8b3cd6");
        composed.setName("Composed String name{}))(/*-+.>,:");
        composed.setNumber(null);
        composed.setAttributes(Arrays.asList("1","2","3"));
        composed.setObjects(objects);
        composed.setDeepObject(deep);
        composed.setIgnoredField(9998);

        Map<String, Object> result = FilterableProcessor.discover(composed);

        Assert.assertNotNull(result);
        Assert.assertEquals(7, result.size());
        Assert.assertThat(result, Matchers.hasEntry("name", "Composed String name{}))(/*-+.>,:"));
        Assert.assertThat(result, Matchers.hasEntry("attributes", Arrays.asList("1","2","3")));
        Assert.assertThat(result, Matchers.hasEntry("objects", objects));
        Assert.assertThat(result, Matchers.hasEntry("deepObject.id", "ad76d0b8-aead-45c8-862b-fd9c8cc0f446"));
        Assert.assertThat(result, Matchers.hasEntry("deepObject.name", "Deep String name{}))(/*-+.>,:"));
        Assert.assertThat(result, Matchers.hasEntry("deepObject.number", 963.08));
        Assert.assertThat(result, Matchers.hasEntry("deepObject.setting", setting));
    }
}
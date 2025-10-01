package api.domain.metrics.controller;

import api.domain.metrics.controller.model.MetricsResponse;
import global.api.Api;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Statistic;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/metrics")
public class MetricsApiController {

    private final MeterRegistry meterRegistry;

    @GetMapping("/requests/all")
    public Api<List<MetricsResponse>> getAllRequestCountsSorted() {
        // http.server.requests 기본 HTTP 요청 메트릭 사용
        List<MetricsResponse> allMetrics = StreamSupport.stream(
                meterRegistry.find("http.server.requests").meters().spliterator(),
                false
            )
            .map(this::toMetricsResponse)
            .filter(mr -> mr != null)
            .sorted((a, b) -> Double.compare(b.getCount(), a.getCount()))
            .collect(Collectors.toList());

        return Api.OK(allMetrics);
    }

    private MetricsResponse toMetricsResponse(Meter meter) {
        // URI 태그 가져오기
        String uri = meter.getId().getTag("uri");
        if (uri == null) return null;

        // COUNT 값 가져오기
        double count = StreamSupport.stream(meter.measure().spliterator(), false)
            .filter(ms -> ms.getStatistic() == Statistic.COUNT)
            .findFirst()
            .map(Measurement::getValue)
            .orElse(0.0);

        return new MetricsResponse(uri, count);
    }

}
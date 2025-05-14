package ureca.ureca_mini.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public List<EventEntity> eventList() {
        return eventRepository.findByEventDateAfter(LocalDateTime.now());
    }

    public EventEntity eventDetail(int id) {
        return eventRepository.findById(id);
    }
}

# Smart Matching Service

Сервис подбора ветеринарных центров по критериям клиента. Принимает запрос с параметрами (район, бюджет, услуги, рейтинг), фильтрует кандидатов, рассчитывает рейтинг соответствия и возвращает отсортированный список центров.

## Технологии

| Компонент | Технология |
|-----------|------------|
| Язык | Kotlin |
| Фреймворк | Ktor |
| Паттерны | Strategy, Specification, Factory |
| Сборка | Gradle |
| Тесты | JUnit 5 |
| Контейнеризация | Docker |

## Архитектура

### Specification — фильтрация кандидатов
- `DistrictSpecification` — фильтрация по району
- `BudgetSpecification` — фильтрация по бюджету
- `ServiceSpecification` — фильтрация по услугам
- `CompositeSpecification` — комбинация фильтров через AND

### Strategy — алгоритмы ранжирования
- `BalancedStrategy` — по рейтингу соответствия (по умолчанию)
- `BestRatingStrategy` — сначала центры с лучшим рейтингом
- `CheapestFirstStrategy` — сначала дешевле
- `ClosestFirstStrategy` — сначала ближе

### Расчёт Score
| Критерий | Баллы |
|----------|-------|
| Район совпадает | +30 |
| В бюджете | +25 |
| Есть нужная услуга | +40 |
| Рейтинг >= предпочтительного | +5 |
| **Максимум** | **100** |

## Структура проекта

```
src/main/kotlin/com/mamonov/
├── model/              — data-классы (DTO)
├── specification/      — фильтрация (Specification паттерн)
├── strategy/           — сортировка (Strategy паттерн)
├── scoring/            — подсчёт баллов
├── service/            — основная логика и тестовые данные
├── factory/            — фабрика стратегий
├── Application.kt      — точка входа
├── Routing.kt          — HTTP маршруты
├── Serialization.kt    — настройка JSON
└── Monitoring.kt       — логирование запросов
```

## Запуск

### Локально
```bash
./gradlew run
```

### Через Docker
```bash
docker build -t smart-matching .
docker run -p 8080:8080 smart-matching
```

## Использование

### Запрос
```http
POST http://localhost:8080/match?strategy=balanced
Content-Type: application/json

{
  "district": "Центральный",
  "maxBudget": 5000,
  "requiredServices": ["Вакцинация", "Стрижка"],
  "preferredRating": 4.5
}
```

### Параметр strategy
| Значение | Описание |
|----------|----------|
| `balanced` | По рейтингу соответствия (по умолчанию) |
| `cheapest` | Сначала дешевле |
| `rating` | Сначала лучший рейтинг |
| `closest` | Сначала ближе |

### Ответ
```json
{
  "centers": [
    { "centerId": 1, "centerName": "Лапки", "score": 100 },
    { "centerId": 4, "centerName": "Мурка", "score": 95 }
  ]
}
```

## Тесты
```bash
./gradlew test
```

Покрыты:
- `MatchingService` — фильтрация и сортировка
- `ScoreCalculator` — подсчёт баллов
- `DistrictSpecification`, `BudgetSpecification`, `ServiceSpecification` — фильтры

## Примечание об архитектуре

В текущей реализации список центров хранится в памяти (`SampleData.kt`) согласно требованиям задания. В production-версии данные приходили бы из БД через repository-слой, а итоговый результат матчинга записывался бы обратно в БД.
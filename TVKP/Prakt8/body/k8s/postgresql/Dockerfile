FROM postgres:15

# Установка необходимых пакетов
RUN apt-get update && apt-get install -y \
    build-essential \
    libssl-dev \
    zlib1g-dev \
    git

# Клонирование репозитория с расширением OpenTelemetry для PostgreSQL
RUN git clone https://github.com/open-telemetry/opentelemetry-cpp.git /usr/src/opentelemetry-cpp

# Сборка и установка расширения
RUN cd /usr/src/opentelemetry-cpp && \
    mkdir build && \
    cd build && \
    cmake .. && \
    make && \
    make install

# Очистка
RUN apt-get remove -y build-essential git && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/* /usr/src/opentelemetry-cpp

# Установка переменных окружения для OpenTelemetry
ENV OTEL_TRACES_EXPORTER otlp
ENV OTEL_EXPORTER_OTLP_ENDPOINT http://otel-collector:4317

# Команда запуска PostgreSQL
CMD ["postgres"]

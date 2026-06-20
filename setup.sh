#!/bin/bash

# ========================================
# SCRIPT DE INSTALACIÓN Y CORRECCIÓN
# FORO JDM - SOLUCIÓN COMPLETA
# ========================================

echo "🚀 Iniciando setup del proyecto Foro JDM..."

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar requisitos
echo -e "${BLUE}📋 Verificando requisitos...${NC}"

if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker no está instalado${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose no está instalado${NC}"
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java no está instalado${NC}"
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Maven no está instalado${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Todos los requisitos están instalados${NC}"

# Navegar al directorio
cd "$(dirname "$0")"

echo ""
echo -e "${BLUE}📦 Paso 1: Construyendo imágenes Docker...${NC}"
docker-compose build

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error al construir imágenes${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Imágenes construidas exitosamente${NC}"

echo ""
echo -e "${BLUE}🚀 Paso 2: Levantando servicios...${NC}"
docker-compose up -d

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error al levantar servicios${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Servicios levantados${NC}"

echo ""
echo -e "${BLUE}⏳ Esperando a que MySQL esté listo...${NC}"
sleep 15

echo ""
echo -e "${BLUE}🧪 Paso 3: Ejecutando pruebas unitarias...${NC}"
for SERVICE in eureka-server api-gateway ms-usuarios ms_categorias ms-hilos ms-comentarios ms-reacciones ms-notificaciones; do
    if [ -d "$SERVICE" ]; then
        echo -e "${BLUE}  Testing $SERVICE...${NC}"
        cd "$SERVICE"
        mvn clean test -q
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}  ✅ $SERVICE${NC}"
        else
            echo -e "${RED}  ⚠️  $SERVICE (algunos tests fallaron)${NC}"
        fi
        cd ..
    fi
done

echo ""
echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ INSTALACIÓN COMPLETADA${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"

echo ""
echo -e "${BLUE}📊 SERVICIOS DISPONIBLES:${NC}"
echo ""
echo "  🔐 Eureka Server:"
echo "     URL: http://localhost:8761"
echo ""
echo "  🚪 API Gateway:"
echo "     URL: http://localhost:8080"
echo ""
echo "  👥 MS-Usuarios:"
echo "     URL: http://localhost:8081/swagger-ui.html"
echo ""
echo "  📂 MS-Categorias:"
echo "     URL: http://localhost:8082/swagger-ui.html"
echo ""
echo "  🧵 MS-Hilos:"
echo "     URL: http://localhost:8083/swagger-ui.html"
echo ""
echo "  💬 MS-Comentarios:"
echo "     URL: http://localhost:8084/swagger-ui.html"
echo ""
echo "  👍 MS-Reacciones:"
echo "     URL: http://localhost:8085/swagger-ui.html"
echo ""
echo "  🔔 MS-Notificaciones:"
echo "     URL: http://localhost:8086/swagger-ui.html"
echo ""

echo -e "${BLUE}📝 COMANDOS ÚTILES:${NC}"
echo ""
echo "  Ver logs en tiempo real:"
echo "    docker-compose logs -f"
echo ""
echo "  Detener servicios:"
echo "    docker-compose down"
echo ""
echo "  Reiniciar servicios:"
echo "    docker-compose restart"
echo ""

echo -e "${BLUE}🧪 USUARIOS DE PRUEBA:${NC}"
echo ""
echo "  Admin:"
echo "    Email: admin@foro.com"
echo "    Password: admin123"
echo ""
echo "  Usuario:"
echo "    Email: usuario@foro.com"
echo "    Password: usuario123"
echo ""

echo -e "${GREEN}¡Proyecto listo para la defensa! 🎓${NC}"

.PHONY: all remove_images build_images push_images start_minikube apply_k8s_configmap apply_k8s_dbs apply_k8s_services fresh_deploy teardown_services pause_dbs resume_dbs shutdown_app restart_app remove_minikube

remove_images:
	docker image rm artellas/accounts:$(version) \
	                artellas/loans:$(version) \
	                artellas/cards:$(version) \
	                artellas/configserver:$(version) \
	                artellas/eurekaserver:$(version) \
	                artellas/gatewayserver:$(version)

services := accounts loans cards configserver eurekaserver message gatewayserver

build_images:
	@start=$$(date +%s); \
	for service in $(services); do \
		( \
			echo "==== Building $$service ===="; \
			cd $$service && mvn clean compile jib:dockerBuild && cd - > /dev/null; \
		) & \
	done; \
	wait; \
	end=$$(date +%s); \
	echo "Concurrent build took $$((end - start)) seconds."

push_images:
	@for service in $(services); do \
		( \
			echo "==== Pushing artellas/$$service:$(version) ===="; \
			docker push artellas/$$service:$(version); \
		) & \
	done; \
	wait

start_minikube:
	minikube status >/dev/null 2>&1 || minikube start

stop_minikube:
	minikube stop

remove_minikube:
	minikube delete

apply_k8s_configmap:
	kubectl apply -f k8s/configmaps.yaml

apply_k8s_dbs:
	kubectl apply -f k8s/keycloakdb.yaml \
	              -f k8s/accountsdb.yaml \
	              -f k8s/loansdb.yaml \
	              -f k8s/cardsdb.yaml \

apply_k8s_services:
	kubectl apply -f k8s/keycloak.yaml \
	              -f k8s/configserver.yaml \
	              -f k8s/eurekaserver.yaml \
	              -f k8s/accounts.yaml \
	              -f k8s/loans.yaml \
	              -f k8s/cards.yaml \
	              -f k8s/gateway.yaml \
	              -f k8s/ingress.yaml

fresh_deploy: remove_images build_images push_images start_minikube apply_k8s_configmap apply_k8s_dbs apply_k8s_services
	@echo "==== 🚀 Deployment completed successfully ===="

teardown_services:
	@echo "==== Deleting all services and deployments except DBs ===="
	kubectl delete -f k8s/gateway.yaml \
	              -f k8s/accounts.yaml \
	              -f k8s/loans.yaml \
	              -f k8s/cards.yaml \
	              -f k8s/eurekaserver.yaml \
	              -f k8s/configserver.yaml \
	              -f k8s/keycloak.yaml \
	              -f k8s/configmaps.yaml --ignore-not-found
	@echo "==== Services deleted, DBs are preserved. ===="

pause_dbs:
	kubectl scale deployment accountsdb --replicas=0
	kubectl scale deployment loansdb --replicas=0
	kubectl scale deployment cardsdb --replicas=0
	kubectl scale deployment keycloakdb --replicas=0
	@echo "==== Database deployments paused. PVCs are preserved. ===="

resume_dbs:
	kubectl scale deployment accountsdb --replicas=1
	kubectl scale deployment loansdb --replicas=1
	kubectl scale deployment cardsdb --replicas=1
	kubectl scale deployment keycloakdb --replicas=1
	@echo "==== Database deployments resumed. ===="

shutdown_app:
	$(MAKE) teardown_services
	$(MAKE) pause_dbs
	$(MAKE) stop_minikube
	@echo "==== 🚀 Application shutdown completed successfully ===="

restart_app:
	$(MAKE) start_minikube
	$(MAKE) apply_k8s_configmap
	$(MAKE) resume_dbs
	$(MAKE) apply_k8s_services
	@echo "==== 🚀 Application restart completed successfully ===="

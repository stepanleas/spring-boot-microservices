remove_images:
	docker image rm artellas/accounts:$(version) \
	                artellas/loans:$(version) \
	                artellas/cards:$(version) \
	                artellas/configserver:$(version) \
	                artellas/eurekaserver:$(version) \
	                artellas/gatewayserver:$(version)

push_images:
	@for service in $(services); do \
		( \
			echo "==== Pushing artellas/$$service:$(version) ===="; \
			docker push artellas/$$service:$(version); \
		) & \
	done; \
	wait


services := accounts loans cards configserver eurekaserver gatewayserver

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

